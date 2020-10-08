/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestListDTO extends BaseDTO {

    private Long contestId;

    private Date gmtCreate;

    private Date gmtModified;

    private Map<String, String> features;

    private String contestTitle;

    private Long userId;

    private Date gmtStart;

    private Date gmtEnd;

    private String source;

    private Integer participantNum;

    // -----------------

    private String username;
}