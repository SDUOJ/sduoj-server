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

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import cn.edu.sdu.qd.oj.common.util.HexStringToLongDeserializer;
import cn.edu.sdu.qd.oj.common.util.LongToHexStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * @Author zhangt2333
 * @Date 2020/3/6 16:03
 * @Version V1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubmissionDTO extends BaseDTO {

    @JsonSerialize(using = LongToHexStringSerializer.class)
    @JsonDeserialize(using = HexStringToLongDeserializer.class)
    private Long submissionId;

    private Date gmtCreate;

    private Date gmtModified;

    private Integer isPublic;

    private Integer valid;

    private Long problemId;

    private Long userId;

    private Long judgeTemplateId;

    private String language;

    private Integer judgeResult;

    private Integer judgeScore;

    private Integer usedTime;

    private Integer usedMemory;

    private Integer codeLength;

    private String judgeLog;

    private String code;

    private List<List<Integer>> checkpointResults; // [ [judgeResult, usedTime, usedMemory], ... ]

    // -----------------------

    private Integer checkpointNum;

    private String username;

    private String problemCode;
}