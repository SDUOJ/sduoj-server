package cn.edu.sdu.qd.oj.submit.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import cn.edu.sdu.qd.oj.common.util.HexStringToLongDeserializer;
import cn.edu.sdu.qd.oj.submit.util.CheckpointResultsToByteDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubmissionUpdateReqDTO extends BaseDTO {

    @NotNull
    @JsonDeserialize(using = HexStringToLongDeserializer.class)
    private Long submissionId;

    private Long judgerId;

    @NotNull
    private Integer judgeResult;

    @NotNull
    private Integer judgeScore;

    @NotNull
    private Integer usedTime;

    @NotNull
    private Integer usedMemory;

    private String resultMessage;

    @NotNull
    @JsonDeserialize(using = CheckpointResultsToByteDeserializer.class)
    private byte[] checkpointResults;
}
