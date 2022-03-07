/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.auth.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PermissionDTO extends BaseDTO {

    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    private Map<String, String> features;

    @NotBlank(message = "permissionUrl不能为空")
    private String url;

    private String name;

    private List<String> roles;
}