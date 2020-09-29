package cn.edu.sdu.qd.oj.user.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(UserSessionDOField.TABLE_NAME)
public class UserSessionDO extends BaseDO {
    
    @TableId(value = UserSessionDOField.ID, type = IdType.AUTO)
    private Long id;

    @TableField(value = UserSessionDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = UserSessionDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(UserSessionDOField.FEATURES)
    private String features;

    @TableField(UserSessionDOField.USERNAME)
    private String username;

    @TableField(UserSessionDOField.IPV4)
    private String ipv4;

    @TableField(UserSessionDOField.USER_AGENT)
    private String userAgent;

    @TableField(UserSessionDOField.SUCCESS)
    private Integer success;

}
