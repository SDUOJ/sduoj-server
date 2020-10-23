/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.judgetemplate.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JudgeTemplateManageListDTO extends BaseDTO {

    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    private String features;

    private Integer version;

    private Integer isPublic;

    private Long userId;

    private Integer type;

    private String title;

    private Long zipFileId;

    private List<String> acceptFileExtensions;

    private String remoteOj;

    private String comment;
}