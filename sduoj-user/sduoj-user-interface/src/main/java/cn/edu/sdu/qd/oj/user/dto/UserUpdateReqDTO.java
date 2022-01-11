/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserUpdateReqDTO extends BaseDTO {

    private Long userId;

    @NotBlank(message = "昵称不能为空")
    @Length(max = 30, message = "昵称长度不合法，比如在30位之内")
    private String nickname;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Length(min = 4, max = 32, message = "密码长度必须在4-32位之间")
    @NotBlank(message = "密码不能为空")
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;

    @Length(min = 11, max = 16, message = "手机号码长度不合法")
    private String phone;

    @Range(min = 0, max = 2, message = "性别不合法, 0.女, 1.男, 2.问号")
    private Integer gender;

    @Length(max = 20, message = "学号长度不合法")
    private String studentId;

    // -----------------------

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String captchaId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String captcha;
}