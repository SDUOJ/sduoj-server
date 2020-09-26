/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
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
@TableName(UserDOField.TABLE_NAME)
public class UserDO extends BaseDO {

    @TableId(value = UserDOField.ID, type = IdType.AUTO)
    private Integer userId;

    @TableField(UserDOField.USERNAME)
    private String username;

    @TableField(UserDOField.PASSWORD)
    private String password;

    @TableField(UserDOField.EMAIL)
    private String email;

    @TableField(UserDOField.GENDER)
    private Integer gender;

    @TableField(UserDOField.STUDENT_ID)
    private String studentId;

    @TableField(UserDOField.CREATE_TIME)
    private Date createTime;

    @TableField(UserDOField.ROLE)
    private String roles;
}
