/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

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

    @Length(max = 30, message = "昵称长度不合法，比如在30位之内")
    private String nickname;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Length(min = 4, max = 32, message = "密码长度必须在4-32位之间")
    @NotBlank(message = "密码不能为空")
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newEmail;

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
