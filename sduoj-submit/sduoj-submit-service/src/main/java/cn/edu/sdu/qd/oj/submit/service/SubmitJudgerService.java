/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.service;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.submit.entity.SubmissionDO;
import cn.edu.sdu.qd.oj.submit.entity.SubmissionJudgeDO;
import cn.edu.sdu.qd.oj.submit.mapper.SubmissionJudgeDOMapper;
import cn.edu.sdu.qd.oj.submit.mapper.SubmissionDOMapper;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionJudgeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    private SubmissionDOMapper submissionDOMapper;

    @Autowired
    private SubmissionJudgeDOMapper submissionJudgeDOMapper;


    public SubmissionJudgeDTO query(long submissionId) {
        SubmissionJudgeDO submissionJudgeDO = this.submissionJudgeDOMapper.selectByPrimaryKey(submissionId);
        if (submissionJudgeDO == null) {
            throw new ApiException(ApiExceptionEnum.SUBMISSION_NOT_FOUND);
        }
        SubmissionJudgeDTO submissionJudgeDTO = new SubmissionJudgeDTO();
        BeanUtils.copyProperties(submissionJudgeDO, submissionJudgeDTO);
        return submissionJudgeDTO;
    }

    public void updateSubmission(SubmissionDTO submissionDTO) {
        SubmissionDO submissionDO = new SubmissionDO();
        BeanUtils.copyProperties(submissionDTO, submissionDO);
        if(this.submissionDOMapper.updateByPrimaryKeySelective(submissionDO) != 1)
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
    }
}