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
import cn.edu.sdu.qd.oj.submit.converter.SubmissionConverter;
import cn.edu.sdu.qd.oj.submit.converter.SubmissionListConverter;
import cn.edu.sdu.qd.oj.submit.dao.SubmissionDao;
import cn.edu.sdu.qd.oj.submit.entity.SubmissionDO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListDTO;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private SubmissionDao submissionDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserClient userClient;

    @Autowired
    private UserCacheUtils userCacheUtils;

    @Autowired
    private ProblemCacheUtils problemCacheUtils;

    @Autowired
    private SubmissionConverter submissionConverter;

    @Autowired
    private SubmissionListConverter submissionListConverter;

    // TODO: 临时采用 IP+PID 格式, 生产时加配置文件 Autowired
    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    @Transactional
    public boolean createSubmission(SubmissionDTO submissionDTO) {
        long snowflaskId = snowflakeIdWorker.nextId();
        submissionDTO.setSubmissionId(snowflaskId);

        SubmissionDO submissionDO = submissionConverter.from(submissionDTO);
        if (!submissionDao.save(submissionDO)) {
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
        SubmissionDO submissionDO = submissionDao.getById(submissionId);
        // 取 checkpointNum
        if (submissionDO != null) {
            submissionDO.setCheckpointNum(problemCacheUtils.getProblemCheckpointNum(submissionDO.getProblemId()));
            return submissionConverter.to(submissionDO);
        }
        return null;
    }

    public PageResult<SubmissionListDTO> querySubmissionByPage(String username, Integer problemId, int pageNow, int pageSize) {
        Long userId = null;
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
        LambdaQueryChainWrapper<SubmissionDO> queryChainWrapper = submissionDao.lambdaQuery().select(
            SubmissionDO::getSubmissionId,
            SubmissionDO::getProblemId,
            SubmissionDO::getUserId,
            SubmissionDO::getLanguageId,
            SubmissionDO::getCreateTime,
            SubmissionDO::getJudgeTime,
            SubmissionDO::getJudgeResult,
            SubmissionDO::getJudgeScore,
            SubmissionDO::getUsedTime,
            SubmissionDO::getUsedMemory,
            SubmissionDO::getCodeLength
        ).orderByDesc(SubmissionDO::getCreateTime);
        if (userId != null) {
            queryChainWrapper.eq(SubmissionDO::getUserId, userId);
        }
        if (problemId != null) {
            queryChainWrapper.eq(SubmissionDO::getProblemId, problemId);
        }
        Page<SubmissionDO> pageResult = queryChainWrapper.page(new Page<>(pageNow, pageSize));
        List<SubmissionListDTO> submissionListDTOList = submissionListConverter.to(pageResult.getRecords());
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
        return new PageResult<>(pageResult.getPages(), submissionListDTOList);
    }

}