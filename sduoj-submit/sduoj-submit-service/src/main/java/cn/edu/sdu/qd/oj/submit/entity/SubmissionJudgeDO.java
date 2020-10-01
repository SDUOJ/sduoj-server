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
public class SubmissionJudgeDO extends BaseDO {


    @TableId(value = SubmissionDOField.ID)
    private Long submissionId;

    @TableField(SubmissionDOField.PROBLEM_ID)
    private Long problemId;

    @TableField(SubmissionDOField.USER_ID)
    private Long userId;

    @TableField(SubmissionDOField.LANGUAGE)
    private String language;

    @TableField(value = SubmissionDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(SubmissionDOField.CODE)
    private String code;

    @TableField(SubmissionDOField.CODE_LENGTH)
    private String codeLength;
}