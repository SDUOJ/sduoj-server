/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.service;

import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.common.util.ProblemCacheUtils;
import cn.edu.sdu.qd.oj.common.util.SnowflakeIdWorker;
import cn.edu.sdu.qd.oj.common.util.UserCacheUtils;
import cn.edu.sdu.qd.oj.submit.client.UserClient;
import cn.edu.sdu.qd.oj.submit.entity.SubmissionDO;
import cn.edu.sdu.qd.oj.submit.entity.SubmissionListDO;
import cn.edu.sdu.qd.oj.submit.mapper.SubmissionListDOMapper;
import cn.edu.sdu.qd.oj.submit.mapper.SubmissionDOMapper;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListDTO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName SubmitService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/6 16:04
 * @Version V1.0
 **/

@Service
@Slf4j
public class SubmitService {

    @Autowired
    private SubmissionDOMapper submissionDOMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SubmissionListDOMapper submissionListDOMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private UserCacheUtils userCacheUtils;

    @Autowired
    private ProblemCacheUtils problemCacheUtils;

    // TODO: 临时采用 IP+PID 格式, 生产时加配置文件 Autowired
    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    @Transactional
    public boolean createSubmission(SubmissionDTO submissionDTO) {
        long snowflaskId = snowflakeIdWorker.nextId();
        submissionDTO.setSubmissionId(snowflaskId);
        SubmissionDO submissionDO = new SubmissionDO();
        BeanUtils.copyProperties(submissionDTO, submissionDO);

        if (this.submissionDOMapper.insertSelective(submissionDO) == 1) {
            try {
                Map<String, Object> msg = new HashMap<>();
                msg.put("event", "submissionCreated");
                msg.put("submissionId", Long.toHexString(submissionDO.getSubmissionId()));
                this.rabbitTemplate.convertAndSend("", "judge_queue", msg);
            } catch (Exception e) {
                log.error("[submit] 提交创建失败", e);
                throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
            }
            return true;
        }
        return false;
    }


    public SubmissionDTO queryById(long submissionId) {
        SubmissionDO submissionDO = this.submissionDOMapper.selectByPrimaryKey(submissionId);
        // 取 checkpointNum
        if (submissionDO != null) {
            submissionDO.setCheckpointNum(problemCacheUtils.getProblemCheckpointNum(submissionDO.getProblemId()));
            SubmissionDTO submissionDTO = new SubmissionDTO();
            BeanUtils.copyProperties(submissionDO, submissionDTO);
            return submissionDTO;
        }
        return null;
    }

    public PageResult<SubmissionListDTO> querySubmissionByPage(String username, Integer problemId, int pageNow, int pageSize) {
        Integer userId = null;
        if (StringUtils.isNotBlank(username)) {
            try {
                userId = userClient.queryUserId(username);
                if (userId == null) {
                    return null;
                }
            } catch (InternalApiException ignore) {
                // TODO: 异常处理
                log.error("[Submission] ", ignore.getMessage());
            }
        }
        Example example = new Example(SubmissionListDO.class);
        example.orderBy("createTime").desc();
        if (userId != null) {
            example.createCriteria().andEqualTo("userId", userId);
        }
        if (problemId != null) {
            example.createCriteria().andEqualTo("problemId", problemId);
        }
        PageHelper.startPage(pageNow, pageSize);
        Page<SubmissionListDO> pageInfo = (Page<SubmissionListDO>) submissionListDOMapper.selectByExample(example);
        List<SubmissionListDTO> submissionListDTOList = pageInfo.stream().map(submissionListDO -> {
            SubmissionListDTO submissionListDTO = new SubmissionListDTO();
            BeanUtils.copyProperties(submissionListDO, submissionListDTO);
            return submissionListDTO;
        }).collect(Collectors.toList());
        if (problemId != null) {
            String problemTitle = problemCacheUtils.getProblemTitle(problemId);
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setProblemTitle(problemTitle));
        } else {
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setProblemTitle(problemCacheUtils.getProblemTitle(submissionListDTO.getProblemId())));
        }
        if (userId != null) {
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setUsername(username));
        } else {
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setUsername(userCacheUtils.getUsername(submissionListDTO.getUserId())));
        }
        return new PageResult<>(pageInfo.getPages(), submissionListDTOList);
    }

}