/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.service;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.submit.converter.SubmissionConverter;
import cn.edu.sdu.qd.oj.submit.converter.SubmissionJudgeConverter;
import cn.edu.sdu.qd.oj.submit.dao.SubmissionDao;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionUpdateReqDTO;
import cn.edu.sdu.qd.oj.submit.entity.SubmissionDO;
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
    private SubmissionDao submissionDao;

    @Autowired
    private SubmissionJudgeConverter submissionJudgeConverter;

    @Autowired
    private SubmissionConverter submissionConverter;

    public SubmissionJudgeDTO query(long submissionId) {
        SubmissionDO submissionJudgeDO = submissionDao.lambdaQuery().select(
                SubmissionDO::getSubmissionId,
                SubmissionDO::getProblemId,
                SubmissionDO::getUserId,
                SubmissionDO::getLanguage,
                SubmissionDO::getGmtCreate,
                SubmissionDO::getCode,
                SubmissionDO::getCodeLength
        ).eq(SubmissionDO::getSubmissionId, submissionId).one();
        if (submissionJudgeDO == null) {
            throw new ApiException(ApiExceptionEnum.SUBMISSION_NOT_FOUND);
        }
        return submissionJudgeConverter.to(submissionJudgeDO);
    }

    public void updateSubmission(SubmissionUpdateReqDTO reqDTO) {
        SubmissionDO submissionDO = new SubmissionDO();
        BeanUtils.copyProperties(reqDTO, submissionDO);
        if (!submissionDao.updateById(submissionDO)) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
    }
}