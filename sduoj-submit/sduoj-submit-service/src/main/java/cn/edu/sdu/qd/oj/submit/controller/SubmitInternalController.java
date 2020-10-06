package cn.edu.sdu.qd.oj.submit.controller;

import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.submit.api.SubmissionApi;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionCreateReqDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListReqDTO;
import cn.edu.sdu.qd.oj.submit.service.SubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubmitInternalController implements SubmissionApi {

    @Autowired
    private SubmitService submitService;

    @Override
    public long create(long contestId, SubmissionCreateReqDTO reqDTO) {
        return submitService.createSubmission(reqDTO, contestId);
    }

    @Override
    public PageResult<SubmissionListDTO> list(long contestId, SubmissionListReqDTO reqDTO) throws InternalApiException {
        return submitService.querySubmissionByPage(reqDTO, contestId);
    }

    @Override
    public SubmissionDTO query(long submissionId, long contestId) throws InternalApiException {
        return submitService.queryById(submissionId, contestId);
    }
}
