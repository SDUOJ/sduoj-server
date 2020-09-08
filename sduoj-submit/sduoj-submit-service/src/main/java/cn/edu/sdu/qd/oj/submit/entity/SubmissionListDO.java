/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = SubmissionDOField.TABLE_NAME)
public class SubmissionListDO extends BaseDO {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = SubmissionDOField.ID)
    private Long submissionId;

    @Column(name = SubmissionDOField.PROBLEM_ID)
    private Integer problemId;

    @Column(name = SubmissionDOField.USER_ID)
    private Integer userId;

    @Column(name = SubmissionDOField.LANGUAGE_ID)
    private Integer languageId;

    @Column(name = SubmissionDOField.CREATE_TIME)
    private Date createTime;

    @Column(name = SubmissionDOField.JUDGE_TIME)
    private Date judgeTime;

    @Column(name = SubmissionDOField.JUDGE_RESULT)
    private Integer judgeResult;

    @Column(name = SubmissionDOField.JUDGE_SCORE)
    private Integer judgeScore;

    @Column(name = SubmissionDOField.USED_TIME)
    private Integer usedTime;

    @Column(name = SubmissionDOField.USED_MEMORY)
    private Integer usedMemory;

    @Column(name = SubmissionDOField.CODE_LENGTH)
    private Integer codeLength;

    @Transient
    private String problemTitle;

    @Transient
    private String username;
}