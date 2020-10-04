package cn.edu.sdu.qd.oj.problem.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(ProblemTagDOField.TABLE_NAME)
public class ProblemTagDO extends BaseDO {

    @TableId(value = ProblemTagDOField.ID, type = IdType.AUTO)
    private Long id;

    @TableField(value = ProblemTagDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = ProblemTagDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(ProblemTagDOField.FEATURES)
    private String features;

    @TableField(ProblemTagDOField.IS_DELETED)
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField(ProblemTagDOField.PARENT_ID)
    private Long parentId;

    @TableField(ProblemTagDOField.TITLE)
    private String title;
}
