/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.pojo;

import cn.edu.sdu.qd.oj.common.config.DateToTimestampSerializer;
import cn.edu.sdu.qd.oj.common.utils.HexStringToLongDeserializer;
import cn.edu.sdu.qd.oj.common.utils.LongToHexStringSerializer;
import cn.edu.sdu.qd.oj.submit.utils.BytesToCheckpointResultsSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName Submission
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/6 16:03
 * @Version V1.0
 **/
@Data
@Table(name = "oj_submissions")
@NoArgsConstructor
@AllArgsConstructor
public class Submission implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "s_id")
    @JsonSerialize(using = LongToHexStringSerializer.class)
    @JsonDeserialize(using = HexStringToLongDeserializer.class)
    private Long submissionId;

    @Column(name = "p_id")
    private Integer problemId;

    @Column(name = "u_id")
    private Integer userId;

    @Column(name = "l_id")
    private Integer languageId;

    @Column(name = "s_create_time")
    @JsonSerialize(using = DateToTimestampSerializer.class)
    private Date createTime;

    @Column(name = "s_judge_time")
    @JsonSerialize(using = DateToTimestampSerializer.class)
    private Date judgeTime;

    @Column(name = "s_ipv4")
    private String ipv4;

    @Column(name = "s_judger_id")
    private Integer judgerId;

    @Column(name = "s_judge_result")
    private Integer judgeResult;

    @Column(name = "s_judge_score")
    private Integer judgeScore;

    @Column(name = "s_used_time")
    private Integer usedTime;

    @Column(name = "s_used_memory")
    private Integer usedMemory;

    @Column(name = "s_judge_log")
    private String judgeLog;

    @Column(name = "s_code")
    private String code;

    @Column(name = "s_code_length")
    private Integer codeLength;

    @Column(name = "s_checkpoint_results")
    @JsonSerialize(using = BytesToCheckpointResultsSerializer.class)
    private byte[] checkpointResults;


    @Transient
    private Integer checkpointNum;


    public Submission(Integer problemId, Integer userId, Integer languageId, String ipv4, String code) {
        this.problemId = problemId;
        this.userId = userId;
        this.languageId = languageId;
        this.ipv4 = ipv4;
        this.code = code;
        this.codeLength = code.length();
    }

    public Submission(Long submissionId, Integer judgerId, Integer judgeResult, Integer judgeScore, Integer usedTime, Integer usedMemory, String judgeLog) {
        this.submissionId = submissionId;
        this.judgerId = judgerId;
        this.judgeResult = judgeResult;
        this.judgeScore = judgeScore;
        this.usedTime = usedTime;
        this.usedMemory = usedMemory;
        this.judgeLog = judgeLog;
    }
}