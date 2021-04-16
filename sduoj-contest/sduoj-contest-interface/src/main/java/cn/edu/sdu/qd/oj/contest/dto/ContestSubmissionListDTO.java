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
import cn.edu.sdu.qd.oj.common.util.HexStringToLongDeserializer;
import cn.edu.sdu.qd.oj.common.util.LongToHexStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestSubmissionListDTO extends BaseDTO {

    @JsonSerialize(using = LongToHexStringSerializer.class)
    @JsonDeserialize(using = HexStringToLongDeserializer.class)
    private Long submissionId;

    private Date gmtCreate;

    private Date gmtModified;

    private Integer isPublic;

    private Integer valid;

    private Long userId;

    private Long judgeTemplateId;

    private Integer judgeResult;

    private Integer judgeScore;

    private Integer usedTime;

    private Integer usedMemory;

    private Integer codeLength;

    // -----------------------------------

    private String problemCode;

    private String problemTitle;

    private String judgeTemplateTitle;

    private String username;

    private Integer checkpointNum;
}