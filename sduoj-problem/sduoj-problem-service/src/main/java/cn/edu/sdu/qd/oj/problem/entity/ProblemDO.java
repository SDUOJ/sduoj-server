package cn.edu.sdu.qd.oj.problem.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(ProblemDOField.TABLE_NAME)
public class ProblemDO extends BaseDO {

    @TableId(value = ProblemDOField.ID, type = IdType.AUTO)
    private Integer problemId;

    @TableField(ProblemDOField.IS_PUBLIC)
    private Integer isPublic;

    @TableField(ProblemDOField.USER_ID)
    private Integer userId;

    @TableField(ProblemDOField.TITLE)
    private String problemTitle;

    @TableField(ProblemDOField.SUBMIT_NUM)
    private Integer submitNum;

    @TableField(ProblemDOField.ACCEPT_NUM)
    private Integer acceptNum;

    @TableField(ProblemDOField.TIME_LIMIT)
    private Integer timeLimit;

    @TableField(ProblemDOField.MEMORY_LIMIT)
    private Integer memoryLimit;

    @TableField(ProblemDOField.MARKDOWN)
    private String markdown;

    @TableField(ProblemDOField.CHECKPOINT_NUM)
    private Integer checkpointNum;

    @TableField(ProblemDOField.CHECKPOINT_IDS)
    private byte[] checkpointIds;

    @TableField(exist = false)
    private String username;
}
