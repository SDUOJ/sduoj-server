/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import cn.edu.sdu.qd.oj.common.util.CollectionUtils;
import cn.edu.sdu.qd.oj.contest.enums.ContestModeEnum;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionResultDTO;
import cn.edu.sdu.qd.oj.submit.enums.SubmissionJudgeResult;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 比赛排行榜 DTO
 * <a href="https://github.com/SDUOJ/sduoj-server/issues/54">Design ranking data structure and interactive flow in contest.</a>.
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestRankDTO extends BaseDTO {

    private Long userId;
    private boolean official;
    private String username;
    private String nickname;
    private Integer problemNum;
    private List<OneSubmission> submissions;       // [ [problemCode, gmtCreate, judgeScore, judgeResult], ... ]
    private List<OneProblemResult> problemResults; // [ [gmtCreate, judgeScore, judgeResult, submissionNum, pendingNum], ... ]


    public static List<ContestRankDTO> create(Map<Long, List<SubmissionResultDTO>> userIdToSubmissionListMap, int problemNum) {
        return userIdToSubmissionListMap.entrySet().stream().map(entry -> {
            List<OneSubmission> submissions = entry.getValue().stream().filter(submissionResultDTO ->
                SubmissionJudgeResult.CE.code != submissionResultDTO.getJudgeResult() &&
                SubmissionJudgeResult.SE.code != submissionResultDTO.getJudgeResult()
            ).map(OneSubmission::new).collect(Collectors.toList());
            return ContestRankDTO.builder()
                    .userId(entry.getKey())
                    .submissions(submissions)
                    .problemNum(problemNum)
                    .official(true)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * @Description 将提交列表计算为榜单结果，同时清空提交列表
     **/
    public void toComputeProblemResults(ContestModeEnum contestType) {
        if (this.submissions == null) {
            return;
        }
        problemResults = new ArrayList<>(problemNum);
        Map<String, List<OneSubmission>> problemIndexToSubmissionMap = submissions.stream().collect(Collectors.groupingBy(o -> String.valueOf(o.get(0))));
        for (int i = 1; i <= problemNum; i++) {
            List<OneSubmission> problemSubmissions = problemIndexToSubmissionMap.get(String.valueOf(i));
            problemResults.add(computeOneProblemResult(problemSubmissions, contestType));
        }
        // 把提交列表脱敏
        this.submissions = null;
    }

    /**
    * @Description 对数据进行封榜，过滤出封榜后的 submissions，转为 pending
    **/
    public void frozenRank(Date frozenTime) {
        if (CollectionUtils.isNotEmpty(submissions)) {
            submissions.stream().filter(s -> s.getGmtCreate().after(frozenTime)).forEach(s -> {
                s.setJudgeResult(SubmissionJudgeResult.PD.code);
                s.setJudgeScore(0);
            });
        }
    }

    /**
    * @Description 给某人同一题的所有提交，计算出该榜单单元格结果
    **/
    private OneProblemResult computeOneProblemResult(List<OneSubmission> problemSubmissions, ContestModeEnum contestType) {
        // 根据提交算 oneProblemResult
        int pendingNum = 0;
        if (!CollectionUtils.isEmpty(problemSubmissions)) {
            switch (contestType) {
                case OI:
                    break;
                case IOI:
                    OneSubmission betterSubmission = problemSubmissions.get(0);
                    for (OneSubmission oneSubmission : problemSubmissions) {
                        int compareScore = betterSubmission.getJudgeScore() - oneSubmission.getJudgeScore();
                        if (compareScore < 0 || (compareScore == 0 && (betterSubmission.getGmtCreate()).compareTo(oneSubmission.getGmtCreate()) > 0)) {
                            betterSubmission = oneSubmission;
                        }
                        if (SubmissionJudgeResult.PD.code == oneSubmission.getJudgeResult()) {
                            pendingNum++;
                        }
                    }
                    return new OneProblemResult(betterSubmission.getGmtCreate(), betterSubmission.getJudgeScore(),
                            betterSubmission.getJudgeResult(), problemSubmissions.size(), pendingNum);
                case ACM:
                    boolean existPD = false;
                    for (OneSubmission oneSubmission : problemSubmissions) {
                        if (SubmissionJudgeResult.PD.code == oneSubmission.getJudgeResult()) {
                            pendingNum++;
                        }
                    }

                    // 按时间排序找到最早 AC，返回
                    problemSubmissions.sort(Comparator.comparing(o -> ((Date) o.get(1))));
                    for (int i1 = 0; i1 < problemSubmissions.size(); i1++) {
                        OneSubmission oneSubmission = problemSubmissions.get(i1);
                        if (SubmissionJudgeResult.AC.code == oneSubmission.getJudgeResult()) {
                            return new OneProblemResult(oneSubmission.getGmtCreate(), oneSubmission.getJudgeScore(),
                                    oneSubmission.getJudgeResult(), i1 + 1, pendingNum);
                        }
                        existPD |= SubmissionJudgeResult.PD.code == oneSubmission.getJudgeResult();
                    }

                    // 返回 PD
                    if (existPD) {
                        return new OneProblemResult(new Date(0), 0, SubmissionJudgeResult.PD.code, problemSubmissions.size(), pendingNum);
                    }
                    // 返回 WA，因为 ACM 里只有 AC、WA、PD，而 TLE、MLE 都作为 WA
                    return new OneProblemResult(new Date(0), 0, SubmissionJudgeResult.WA.code, problemSubmissions.size(), 0);
            }
        }
        return new OneProblemResult();
    }

    public static class OneSubmission extends ArrayList<Object> {

        public static final int INDEX_PROBLEM_CODE = 0;
        public static final int INDEX_GMT_CREATE = 1;
        public static final int INDEX_JUDGE_SCORE = 2;
        public static final int INDEX_JUDGE_RESULT = 3;

        public OneSubmission() {
            super();
        }

        public OneSubmission(String problemCode, Date gmtCreate, Integer judgeScore, Integer judgeResult) {
            super(4);
            super.add(INDEX_PROBLEM_CODE, problemCode);
            super.add(INDEX_GMT_CREATE, gmtCreate);
            super.add(INDEX_JUDGE_SCORE, judgeScore);
            super.add(INDEX_JUDGE_RESULT, judgeResult);
        }

        public OneSubmission(SubmissionResultDTO submissionResultDTO) {
            this(submissionResultDTO.getProblemCode(), submissionResultDTO.getGmtCreate(), submissionResultDTO.getJudgeScore(), submissionResultDTO.getJudgeResult());
        }

        public String getProblemCode() {
            return (String) super.get(INDEX_PROBLEM_CODE);
        }

        public Date getGmtCreate() {
            return (Date) super.get(INDEX_GMT_CREATE);
        }

        public Integer getJudgeScore() {
            return (Integer) super.get(INDEX_JUDGE_SCORE);
        }

        public void setJudgeScore(Integer judgeScore) {
            super.set(INDEX_JUDGE_SCORE, judgeScore);
        }

        public Integer getJudgeResult() {
            return (Integer) super.get(INDEX_JUDGE_RESULT);
        }

        public void setJudgeResult(Integer judgeResult) {
            super.set(INDEX_JUDGE_RESULT, judgeResult);
        }
    }

    public static class OneProblemResult extends ArrayList<Object> {
        public static final int INDEX_GMT_CREATE = 0;
        public static final int INDEX_JUDGE_SCORE = 1;
        public static final int INDEX_JUDGE_RESULT = 2;
        public static final int INDEX_SUBMISSION_NUM = 3;
        public static final int INDEX_PENDING_NUM = 4;    // TODO: 实现封榜功能

        public OneProblemResult() {
            super();
        }

        public OneProblemResult(Date gmtCreate, Integer judgeScore, Integer judgeResult, Integer submissionNum, Integer pendingNum) {
            super(4);
            super.add(INDEX_GMT_CREATE, gmtCreate);
            super.add(INDEX_JUDGE_SCORE, judgeScore);
            super.add(INDEX_JUDGE_RESULT, judgeResult);
            super.add(INDEX_SUBMISSION_NUM, submissionNum);
            super.add(INDEX_PENDING_NUM, pendingNum);
        }

        public Date getGmtCreate() {
            return (Date) super.get(INDEX_GMT_CREATE);
        }

        public Integer getJudgeScore() {
            return (Integer) super.get(INDEX_JUDGE_SCORE);
        }

        public Integer getJudgeResult() {
            return (Integer) super.get(INDEX_JUDGE_RESULT);
        }

        public String getSubmissionNum() {
            return (String) super.get(INDEX_SUBMISSION_NUM);
        }

        public Integer getPendingNum() {
            return (Integer) super.get(INDEX_PENDING_NUM);
        }
    }
}
