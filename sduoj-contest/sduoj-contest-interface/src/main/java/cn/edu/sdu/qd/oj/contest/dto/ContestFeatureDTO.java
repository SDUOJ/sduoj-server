/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestFeatureDTO extends BaseDTO {

    @NotNull
    private String mode;

    @NotNull
    private String openness;

    @NotNull
    private Integer frozenTime;          // 榜单冻结倒计时 min

    @NotNull
    @Valid
    private InfoOpenness contestRunning; // 赛时信息可见性

    @NotNull
    @Valid
    private InfoOpenness contestEnd;     // 赛后信息可见性

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class InfoOpenness extends BaseDTO {

        @NotNull
        private Integer displayPeerSubmission;

        @NotNull
        private Integer displayRank;

        @NotNull
        private Integer displayJudgeScore;

        @NotNull
        private Integer displayCheckpointResult;
    }
}
