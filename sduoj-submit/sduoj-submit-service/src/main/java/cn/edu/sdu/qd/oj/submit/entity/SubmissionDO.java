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
public class SubmissionDO extends BaseDO {

    @TableId(value = SubmissionDOField.ID)
    private Long submissionId;

    @TableField(SubmissionDOField.PROBLEM_ID)
    private Long problemId;

    @TableField(SubmissionDOField.USER_ID)
    private Integer userId;

    @TableField(SubmissionDOField.LANGUAGE_ID)
    private Integer languageId;

    @TableField(SubmissionDOField.CREATE_TIME)
    private Date createTime;

    @TableField(SubmissionDOField.JUDGE_TIME)
    private Date judgeTime;

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

    @TableField(SubmissionDOField.JUDGE_LOG)
    private String judgeLog;

    @TableField(SubmissionDOField.CODE)
    private String code;

    @TableField(SubmissionDOField.CODE_LENGTH)
    private Integer codeLength;

    @TableField(SubmissionDOField.CHECKPOINT_RESULTS)
    private byte[] checkpointResults;

    @TableField(exist = false)
    private Integer checkpointNum;


    public SubmissionDO(Long problemId, Integer userId, Integer languageId, String ipv4, String code) {
        this.problemId = problemId;
        this.userId = userId;
        this.languageId = languageId;
        this.ipv4 = ipv4;
        this.code = code;
        this.codeLength = code.length();
    }

    public SubmissionDO(Long submissionId, Integer judgerId, Integer judgeResult, Integer judgeScore, Integer usedTime, Integer usedMemory, String judgeLog) {
        this.submissionId = submissionId;
        this.judgerId = judgerId;
        this.judgeResult = judgeResult;
        this.judgeScore = judgeScore;
        this.usedTime = usedTime;
        this.usedMemory = usedMemory;
        this.judgeLog = judgeLog;
    }
}