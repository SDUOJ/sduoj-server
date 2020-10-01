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
@TableName(ProblemDescriptionDOField.TABLE_NAME)
public class ProblemDescriptionDO extends BaseDO {

    @TableId(value = ProblemDescriptionDOField.ID, type = IdType.AUTO)
    private Long id;

    @TableField(value = ProblemDescriptionDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = ProblemDescriptionDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(ProblemDescriptionDOField.FEATURES)
    private String features;

    @TableField(ProblemDescriptionDOField.VERSION)
    private Integer version;

    @TableField(ProblemDescriptionDOField.IS_PUBLIC)
    private Integer deleted;

    @TableField(ProblemDescriptionDOField.IS_PUBLIC)
    private Integer isPublic;

    @TableField(ProblemDescriptionDOField.PROBLEM_ID)
    private Long problemId;

    @TableField(ProblemDescriptionDOField.USER_ID)
    private Long userId;

    @TableField(ProblemDescriptionDOField.VOTE_NUM)
    private Integer voteNum;

    @TableField(ProblemDescriptionDOField.MARKDOWN_DESCRIPTION)
    private String markdownDescription;

    @TableField(ProblemDescriptionDOField.HTML_DESCRIPTION)
    private String htmlDescription;

    @TableField(ProblemDescriptionDOField.HTML_INPUT)
    private String htmlInput;

    @TableField(ProblemDescriptionDOField.HTML_OUTPUT)
    private String htmlOutput;

    @TableField(ProblemDescriptionDOField.HTML_SAMPLE_INPUT)
    private String htmlSampleInput;

    @TableField(ProblemDescriptionDOField.HTML_SAMPLE_OUTOUT)
    private String htmlSampleOutout;

    @TableField(ProblemDescriptionDOField.HTML_HINT)
    private String htmlHint;
}
