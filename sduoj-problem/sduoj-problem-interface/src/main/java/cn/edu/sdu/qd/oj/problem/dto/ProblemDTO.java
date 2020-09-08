package cn.edu.sdu.qd.oj.problem.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemDTO extends BaseDTO {

    private Integer problemId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer isPublic;

    private Integer userId;

    private String problemTitle;

    private Integer submitNum;

    private Integer acceptNum;

    private Integer timeLimit;

    private Integer memoryLimit;

    private String markdown;

    private Integer checkpointNum;
}
