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
@TableName(ProblemDOField.TABLE_NAME)
public class ProblemDO extends BaseDO {

    @TableId(value = ProblemDOField.ID, type = IdType.AUTO)
    private Long problemId;

    @TableField(value = ProblemDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = ProblemDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(ProblemDOField.FEATURES)
    private String features;

    @TableField(ProblemDOField.DELETED)
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField(ProblemDOField.VERSION)
    private Integer version;

    @TableField(ProblemDOField.CODE)
    private String problemCode;

    @TableField(ProblemDOField.IS_PUBLIC)
    private Integer isPublic;

    @TableField(ProblemDOField.USER_ID)
    private Long userId;

    @TableField(ProblemDOField.TITLE)
    private String problemTitle;

    @TableField(ProblemDOField.SOURCE)
    private String source;

    @TableField(ProblemDOField.REMOTE_OJ)
    private String remoteOj;

    @TableField(ProblemDOField.REMOTE_URL)
    private String remoteUrl;

    @TableField(ProblemDOField.SUBMIT_NUM)
    private Integer submitNum;

    @TableField(ProblemDOField.ACCEPT_NUM)
    private Integer acceptNum;

    @TableField(ProblemDOField.LANGUAGES)
    private String languages;

    @TableField(ProblemDOField.MEMORY_LIMIT)
    private Integer memoryLimit;

    @TableField(ProblemDOField.TIME_LIMIT)
    private Integer timeLimit;

    @TableField(ProblemDOField.DEFAULT_DESCRIPTION_ID)
    private Long defaultDescriptionId;

    @TableField(ProblemDOField.CHECKPOINT_NUM)
    private Integer checkpointNum;

    @TableField(ProblemDOField.CHECKPOINTS)
    private byte[] checkpoints;
}
