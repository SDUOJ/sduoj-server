/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.dto;

import java.util.ArrayList;

/**
* @Description 动态评测结果mq传输体
**/
public class CheckpointResultMessageDTO extends ArrayList<Object> {

    // [submissionId, checkpointIndex, judgeScore， judgeResult,  usedTime, usedMemory]

    public static final int INDEX_SUBMISSION_ID = 0;
    public static final int INDEX_CHECKPOINT_INDEX = 1;
    public static final int INDEX_JUDGE_RESULT = 2;
    public static final int INDEX_JUDGE_SCORE = 3;
    public static final int INDEX_USED_TIME = 4;
    public static final int INDEX_USED_MEMORY = 5;

    public EachCheckpointResult toEachCheckpointResult() {
        return new EachCheckpointResult(getJudgeResult(), getJudgeScore(), getUsedTime(), getUsedMemory());
    }

    public CheckpointResultMessageDTO() {
        super();
    }

    public CheckpointResultMessageDTO(Long submissionId, Integer flag) {
        super(2);
        add(INDEX_SUBMISSION_ID, submissionId);
        add(flag);
    }

    public CheckpointResultMessageDTO(Long submissionId, Integer checkpointIndex, Integer judgeResult,
                                      Integer judgeScore, Integer usedTime, Integer usedMemory) {
        super(6);
        super.add(INDEX_SUBMISSION_ID, submissionId);
        super.add(INDEX_CHECKPOINT_INDEX, checkpointIndex);
        super.add(INDEX_JUDGE_RESULT, judgeResult);
        super.add(INDEX_JUDGE_SCORE, judgeScore);
        super.add(INDEX_USED_TIME, usedTime);
        super.add(INDEX_USED_MEMORY, usedMemory);
    }

    public Long getSubmissionId() {
        return (Long) super.get(INDEX_SUBMISSION_ID);
    }

    public Integer getCheckpointIndex() {
        return (Integer) super.get(INDEX_CHECKPOINT_INDEX);
    }

    public Integer getJudgeScore() {
        return (Integer) super.get(INDEX_JUDGE_SCORE);
    }

    public Integer getJudgeResult() {
        return (Integer) super.get(INDEX_JUDGE_RESULT);
    }

    public Integer getUsedTime() {
        return (Integer) super.get(INDEX_USED_TIME);
    }

    public Integer getUsedMemory() {
        return (Integer) super.get(INDEX_USED_MEMORY);
    }
}