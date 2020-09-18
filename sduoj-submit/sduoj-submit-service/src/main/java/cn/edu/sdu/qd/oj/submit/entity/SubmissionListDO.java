/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(SubmissionDOField.TABLE_NAME)
public class SubmissionListDO extends BaseDO {
    private static final long serialVersionUID = 1L;

    @TableId(value = SubmissionDOField.ID)
    private Long submissionId;

    @TableField(SubmissionDOField.PROBLEM_ID)
    private Integer problemId;

    @TableField(SubmissionDOField.USER_ID)
    private Integer userId;

    @TableField(SubmissionDOField.LANGUAGE_ID)
    private Integer languageId;

    @TableField(SubmissionDOField.CREATE_TIME)
    private Date createTime;

    @TableField(SubmissionDOField.JUDGE_TIME)
    private Date judgeTime;

    @TableField(SubmissionDOField.JUDGE_RESULT)
    private Integer judgeResult;

    @TableField(SubmissionDOField.JUDGE_SCORE)
    private Integer judgeScore;

    @TableField(SubmissionDOField.USED_TIME)
    private Integer usedTime;

    @TableField(SubmissionDOField.USED_MEMORY)
    private Integer usedMemory;

    @TableField(SubmissionDOField.CODE_LENGTH)
    private Integer codeLength;

    @TableField(exist = false)
    private String problemTitle;

    @TableField(exist = false)
    private String username;
}