/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.dto;

import cn.edu.sdu.qd.oj.common.config.DateToTimestampSerializer;
import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import cn.edu.sdu.qd.oj.common.utils.HexStringToLongDeserializer;
import cn.edu.sdu.qd.oj.common.utils.LongToHexStringSerializer;
import cn.edu.sdu.qd.oj.submit.util.BytesToCheckpointResultsSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.util.Date;

/**
 * @Author zhangt2333
 * @Date 2020/3/6 16:03
 * @Version V1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubmissionDTO extends BaseDTO {

    @JsonSerialize(using = LongToHexStringSerializer.class)
    @JsonDeserialize(using = HexStringToLongDeserializer.class)
    private Long submissionId;

    private Integer problemId;

    private Integer userId;

    private Integer languageId;

    @JsonSerialize(using = DateToTimestampSerializer.class)
    private Date createTime;

    @JsonSerialize(using = DateToTimestampSerializer.class)
    private Date judgeTime;

    private String ipv4;

    private Integer judgerId;

    private Integer judgeResult;

    private Integer judgeScore;

    private Integer usedTime;

    private Integer usedMemory;

    private String judgeLog;

    private String code;

    private Integer codeLength;

    @JsonSerialize(using = BytesToCheckpointResultsSerializer.class)
    private byte[] checkpointResults;

    private Integer checkpointNum;


    public SubmissionDTO(Integer problemId, Integer userId, Integer languageId, String ipv4, String code) {
        this.problemId = problemId;
        this.userId = userId;
        this.languageId = languageId;
        this.ipv4 = ipv4;
        this.code = code;
        this.codeLength = code.length();
    }

    public SubmissionDTO(Long submissionId, Integer judgerId, Integer judgeResult, Integer judgeScore, Integer usedTime, Integer usedMemory, String judgeLog) {
        this.submissionId = submissionId;
        this.judgerId = judgerId;
        this.judgeResult = judgeResult;
        this.judgeScore = judgeScore;
        this.usedTime = usedTime;
        this.usedMemory = usedMemory;
        this.judgeLog = judgeLog;
    }
}