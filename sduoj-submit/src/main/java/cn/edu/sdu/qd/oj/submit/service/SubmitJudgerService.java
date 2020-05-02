/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.service;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.submit.mapper.SubmissionJudgeBoMapper;
import cn.edu.sdu.qd.oj.submit.mapper.SubmissionMapper;
import cn.edu.sdu.qd.oj.submit.pojo.Submission;
import cn.edu.sdu.qd.oj.submit.pojo.SubmissionJudgeBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName SubmitJudgerService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:49
 * @Version V1.0
 **/

@Service
@Slf4j
public class SubmitJudgerService {
    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private SubmissionJudgeBoMapper submissionJudgeBoMapper;


    public SubmissionJudgeBo query(long submissionId) {
        SubmissionJudgeBo submissionJudgeBo = this.submissionJudgeBoMapper.selectByPrimaryKey(submissionId);
        if (submissionJudgeBo == null) {
            throw new ApiException(ApiExceptionEnum.SUBMISSION_NOT_FOUND);
        }
        return submissionJudgeBo;
    }

    public void updateSubmission(Submission submission) {
        if(this.submissionMapper.updateByPrimaryKeySelective(submission) != 1)
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
    }
}