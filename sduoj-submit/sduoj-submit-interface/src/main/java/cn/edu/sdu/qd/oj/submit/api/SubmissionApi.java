package cn.edu.sdu.qd.oj.submit.api;

import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionCreateReqDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListReqDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/internal/submit")
public interface SubmissionApi {
    String SERVICE_NAME = "submit-service";

    /**
    * @Description 创建一个提交，返回 submissionId
    **/
    @PostMapping(value = "/create", consumes = "application/json")
    long create(@RequestParam("contestId") long contestId,
                @RequestBody SubmissionCreateReqDTO reqDTO);

    @PostMapping(value = "/list", consumes = "application/json")
    PageResult<SubmissionListDTO> list(@RequestParam("contestId") long contestId,
                                       @RequestBody SubmissionListReqDTO reqDTO) throws InternalApiException;
}
