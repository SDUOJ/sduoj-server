package cn.edu.sdu.qd.oj.auth.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(PermissionDOField.TABLE_NAME)
public class PermissionDO extends BaseDO {

    @TableId(value = PermissionDOField.ID, type = IdType.AUTO)
    private Long id;

    @TableField(value = PermissionDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = PermissionDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(PermissionDOField.FEATURES)
    private String features;

    @TableField(PermissionDOField.IS_DELETED)
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField(PermissionDOField.URL)
    private String url;

    @TableField(PermissionDOField.NAME)
    private String name;

    @TableField(PermissionDOField.ROLES)
    private String roles;
}
