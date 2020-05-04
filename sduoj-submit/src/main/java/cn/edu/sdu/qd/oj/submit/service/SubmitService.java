/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.service;

import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.common.utils.ProblemCacheUtils;
import cn.edu.sdu.qd.oj.common.utils.SnowflakeIdWorker;
import cn.edu.sdu.qd.oj.common.utils.UserCacheUtils;
import cn.edu.sdu.qd.oj.submit.client.UserClient;
import cn.edu.sdu.qd.oj.submit.mapper.SubmissionListBoMapper;
import cn.edu.sdu.qd.oj.submit.mapper.SubmissionMapper;
import cn.edu.sdu.qd.oj.submit.pojo.Submission;
import cn.edu.sdu.qd.oj.submit.pojo.SubmissionListBo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
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
    private SubmissionMapper submissionMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SubmissionListBoMapper submissionListBoMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private UserCacheUtils userCacheUtils;

    @Autowired
    private ProblemCacheUtils problemCacheUtils;

    // TODO: 临时采用 IP+PID 格式, 生产时加配置文件 Autowired
    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    @Transactional
    public boolean createSubmission(Submission submission) {
        long snowflaskId = snowflakeIdWorker.nextId();
        submission.setSubmissionId(snowflaskId);
        if (this.submissionMapper.insertSelective(submission) == 1) {
            try {
                Map<String, Object> msg = new HashMap<>();
                msg.put("event", "submissionCreated");
                msg.put("submissionId", submission.getSubmissionId());
                this.rabbitTemplate.convertAndSend("", "judge_queue", msg);
            } catch (Exception e) {
                log.error("[submit] 提交创建失败");
                throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
            }
            return true;
        }
        return false;
    }


    public Submission queryById(long submissionId) {
        Submission submission = this.submissionMapper.selectByPrimaryKey(submissionId);
        // 取 checkpointNum
        if (submission != null)
            submission.setCheckpointNum(problemCacheUtils.getProblemCheckpointNum(submission.getProblemId()));
        return submission;
    }

    public PageResult<SubmissionListBo> querySubmissionByPage(String username, Integer problemId, int pageNow, int pageSize) {
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
        Example example = new Example(SubmissionListBo.class);
        if (userId != null) {
            example.createCriteria().andEqualTo("userId", userId);
        }
        if (problemId != null) {
            example.createCriteria().andEqualTo("problemId", problemId);
        }
        PageHelper.startPage(pageNow, pageSize);
        Page<SubmissionListBo> pageInfo = (Page<SubmissionListBo>) submissionListBoMapper.selectByExample(example);
        if (problemId != null) {
            String problemTitle = problemCacheUtils.getProblemTitle(problemId);
            pageInfo.forEach(submissionListBo -> submissionListBo.setProblemTitle(problemTitle));
        } else {
            pageInfo.forEach(submissionListBo -> submissionListBo.setProblemTitle(problemCacheUtils.getProblemTitle(submissionListBo.getProblemId())));
        }
        if (userId != null) {
            pageInfo.forEach(submissionListBo -> submissionListBo.setUsername(username));
        } else {
            pageInfo.forEach(submissionListBo -> submissionListBo.setUsername(userCacheUtils.getUsername(submissionListBo.getUserId())));
        }
        return new PageResult<>(pageInfo.getPages(), pageInfo);
    }

}