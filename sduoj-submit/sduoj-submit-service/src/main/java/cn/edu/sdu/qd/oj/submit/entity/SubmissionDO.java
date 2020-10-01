/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.FieldFill;
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
public class SubmissionDO extends BaseDO {

    @TableId(value = SubmissionDOField.ID)
    private Long submissionId;

    @TableField(value = SubmissionDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = SubmissionDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(SubmissionDOField.FEATURES)
    private String features;

    @TableField(SubmissionDOField.VERSION)
    private Integer version;

    @TableField(SubmissionDOField.IS_PUBLIC)
    private Integer isPublic;

    @TableField(SubmissionDOField.VALID)
    private Integer valid;

    @TableField(SubmissionDOField.PROBLEM_ID)
    private Long problemId;

    @TableField(SubmissionDOField.USER_ID)
    private Long userId;

    @TableField(SubmissionDOField.CONTEST_ID)
    private Long contestId;

    @TableField(SubmissionDOField.LANGUAGE)
    private String language;

    @TableField(SubmissionDOField.IPV4)
    private String ipv4;

    @TableField(SubmissionDOField.JUDGER_ID)
    private Integer judgerId;

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

    @TableField(SubmissionDOField.JUDGE_LOG)
    private String judgeLog;

    @TableField(SubmissionDOField.CODE)
    private String code;

    @TableField(SubmissionDOField.CHECKPOINT_RESULTS)
    private byte[] checkpointResults;
}