/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.dto;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

public class EachCheckpointResult extends ArrayList<Integer> {
    public static final int INDEX_JUDGE_RESULT = 0;
    public static final int INDEX_JUDGE_SCORE = 1;
    public static final int INDEX_USED_TIME = 2;
    public static final int INDEX_USED_MEMORY = 3;

    public EachCheckpointResult() {
        super();
    }

    public EachCheckpointResult(int judgeResult, int judgeScore, int usedTime, int usedMemory) {
        super(4);
        super.add(INDEX_JUDGE_RESULT, judgeResult);
        super.add(INDEX_JUDGE_SCORE, judgeScore);
        super.add(INDEX_USED_TIME, usedTime);
        super.add(INDEX_USED_MEMORY, usedMemory);
    }

    public EachCheckpointResult(@NotNull CheckpointResultMessageDTO messageDTO) {
        this(messageDTO.getJudgeResult(), messageDTO.getJudgeScore(), messageDTO.getUsedTime(), messageDTO.getUsedMemory());
    }
}