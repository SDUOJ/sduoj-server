package cn.edu.sdu.qd.oj.contest.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.contest.dto.*;
import cn.edu.sdu.qd.oj.contest.service.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/contest")
public class ContestController {

    @Autowired
    private ContestService contestService;


    @GetMapping("/list")
    @ApiResponseBody
    public PageResult<ContestListDTO> list(ContestListReqDTO reqDTO) {
        return contestService.page(reqDTO);
    }

    @PostMapping("/participate")
    @ApiResponseBody
    public Void participate(@RequestBody Map<String, String> json,
                            @RequestHeader("authorization-userId") Long userId) {
        String password = json.get("password");
        Long contestId = Long.valueOf(json.get("contestId"));
        this.contestService.participate(contestId, userId, password);
        return null;
    }

    @GetMapping("/query")
    @ApiResponseBody
    public ContestDTO list(@RequestParam("contestId") @NotBlank Long contestId,
                           @RequestHeader("authorization-userId") long userId) {
        ContestDTO contestDTO = contestService.queryAndValidate(contestId, userId);
        // 脱敏
        int problemIndex = 1;
        for (ContestProblemListDTO problem : contestDTO.getProblems()) {
            problem.setProblemCode(String.valueOf(problemIndex));
            problemIndex++;
        }
        // 比赛未开始
        if (contestDTO.getGmtStart().after(new Date())) {
            contestDTO.setProblems(null);
        }
        return contestDTO;
    }

    @GetMapping("/queryUpcomingContest")
    @ApiResponseBody
    public ContestListDTO queryUpcomingContest() {
        return contestService.queryUpcomingContest();
    }


    @GetMapping("/queryProblem")
    @ApiResponseBody
    public ContestProblemDTO queryProblem(@RequestParam("contestId") @NotBlank Long contestId,
                                          @RequestParam("problemCode") @NotBlank Integer problemIndex,
                                          @RequestHeader("authorization-userId") @NotNull Long userId) {
        ContestProblemDTO contestProblemDTO = contestService.queryProblem(contestId, problemIndex, userId);
        // 脱敏
        contestProblemDTO.setProblemCode(String.valueOf(problemIndex));
        return contestProblemDTO;
    }

    @PostMapping("/createSubmission")
    @ApiResponseBody
    public String submitCode(@RequestBody @Valid ContestSubmissionCreateReqDTO reqDTO,
                             @RequestHeader("X-FORWARDED-FOR") String ipv4,
                             @RequestHeader("authorization-userId") @NotNull Long userId) {
        // 增补数据
        reqDTO.setIpv4(ipv4);
        reqDTO.setUserId(userId);
        try {
            reqDTO.setProblemIndex(Integer.parseInt(reqDTO.getProblemCode()));
        } catch (Exception e){
            throw new ApiException(ApiExceptionEnum.PARAMETER_ERROR);
        }
        return contestService.createSubmission(reqDTO);
    }

    @GetMapping("/listSubmission")
    @ApiResponseBody
    public PageResult<ContestSubmissionListDTO> querySubmission(@RequestBody @Valid ContestSubmissionListReqDTO reqDTO,
                                                                @RequestHeader("authorization-userId") @NotNull Long requestUserId) {
        try {
            reqDTO.setProblemIndex(Integer.parseInt(reqDTO.getProblemCode()));
        } catch (Exception e){
            throw new ApiException(ApiExceptionEnum.PARAMETER_ERROR);
        }
        return contestService.listSubmission(reqDTO, requestUserId);
    }

}
