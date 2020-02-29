/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

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
    @Column(name = "u_id")
    private Integer id;

    @Column(name = "u_username")
    private String username;

    @Column(name = "u_password")
    @JsonIgnore
    private String password;

    @Column(name = "u_nickname")
    private String nickname;

    @Column(name = "u_email")
    private String email;

    @Column(name = "u_gender")
    private Short gender;
}
