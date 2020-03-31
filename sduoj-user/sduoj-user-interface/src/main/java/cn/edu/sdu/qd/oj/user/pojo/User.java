/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.pojo;

import cn.edu.sdu.qd.oj.common.config.DateToTimestampSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName User
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@Data
@Table(name = "oj_users")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "u_id")
    private Integer userId;

    @Column(name = "u_username")
    @Pattern(regexp="^[A-Za-z0-9_]{4,16}$", message="用户名必须由英文、数字、'_'构成，且长度为4~16")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Column(name = "u_password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Length(min = 4, max = 32, message = "密码长度必须在4-32位之间")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Column(name = "u_email")
    @Email(message = "邮箱不合法")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @Column(name = "u_gender")
    @Range(min = 0, max = 2, message = "性别不合法, 0.女, 1.男, 2.问号")
    private Byte gender;

    @Column(name = "u_student_id")
    private String studentId;

    @Column(name = "u_create_time")
    @JsonSerialize(using = DateToTimestampSerializer.class)
    private Date createTime;

    @Column(name = "u_role")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Byte role;
}
