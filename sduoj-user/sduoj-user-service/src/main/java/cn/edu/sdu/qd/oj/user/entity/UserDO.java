/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.entity;

import cn.edu.sdu.qd.oj.common.util.DateToTimestampSerializer;
import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**:q!q
 * @ClassName UserDO
 * @Author zhangt2333
 * @Date 2020/9/7 16:54
 * @Version V1.0
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = UserDOField.TABLE_NAME)
public class UserDO extends BaseDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = UserDOField.ID)
    private Integer userId;

    @Column(name = UserDOField.USERNAME)
    @Pattern(regexp="^[A-Za-z0-9_]{4,16}$", message="用户名必须由英文、数字、'_'构成，且长度为4~16")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Column(name = UserDOField.PASSWORD)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Length(min = 4, max = 32, message = "密码长度必须在4-32位之间")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Column(name = UserDOField.EMAIL)
    @Email(message = "邮箱不合法")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @Column(name = UserDOField.GENDER)
    @Range(min = 0, max = 2, message = "性别不合法, 0.女, 1.男, 2.问号")
    private Integer gender;

    @Column(name = UserDOField.STUDENT_ID)
    private String studentId;

    @Column(name = UserDOField.CREATE_TIME)
    @JsonSerialize(using = DateToTimestampSerializer.class)
    private Date createTime;

    @Column(name = UserDOField.ROLE)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer role;
}
