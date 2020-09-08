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
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = SubmissionDOField.TABLE_NAME)
public class SubmissionJudgeDO extends BaseDO {

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

    @Column(name = SubmissionDOField.CODE)
    private String code;

    @Column(name = SubmissionDOField.CODE_LENGTH)
    private Integer codeLength;
}