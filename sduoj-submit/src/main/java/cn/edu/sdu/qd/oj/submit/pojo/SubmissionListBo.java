/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.pojo;

import cn.edu.sdu.qd.oj.common.config.DateToTimestampSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @ClassName SubmissionListBo
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/5 11:17
 * @Version V1.0
 **/

@Data
@Table(name = "oj_submissions")
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionListBo {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "s_id")
    private Long submissionId;

    @Column(name = "p_id")
    private Integer problemId;

    @Transient
    private String problemTitle;

    @Column(name = "u_id")
    private Integer userId;

    @Transient
    private String username;

    @Column(name = "l_id")
    private Integer languageId;

    @Column(name = "s_create_time")
    @JsonSerialize(using = DateToTimestampSerializer.class)
    private Date createTime;

    @Column(name = "s_judge_time")
    @JsonSerialize(using = DateToTimestampSerializer.class)
    private Date judgeTime;

    @Column(name = "s_judge_result")
    private Integer judgeResult;

    @Column(name = "s_judge_score")
    private Integer judgeScore;

    @Column(name = "s_used_time")
    private Integer usedTime;

    @Column(name = "s_used_memory")
    private Integer usedMemory;

    @Column(name = "s_code_length")
    private Integer codeLength;
}