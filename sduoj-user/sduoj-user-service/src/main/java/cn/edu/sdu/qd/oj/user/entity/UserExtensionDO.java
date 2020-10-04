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
@TableName(UserExtensionDOField.TABLE_NAME)
public class UserExtensionDO extends BaseDO {

    @TableId(value = UserExtensionDOField.ID, type = IdType.AUTO)
    private Long id;

    @TableField(value = UserExtensionDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = UserExtensionDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(UserExtensionDOField.VERSION)
    private Integer version;

    @TableField(UserExtensionDOField.DELETED)
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField(UserExtensionDOField.USER_ID)
    private Long userId;

    @TableField(UserExtensionDOField.KEY)
    private String key;

    @TableField(UserExtensionDOField.VALUE)
    private String value;
}
