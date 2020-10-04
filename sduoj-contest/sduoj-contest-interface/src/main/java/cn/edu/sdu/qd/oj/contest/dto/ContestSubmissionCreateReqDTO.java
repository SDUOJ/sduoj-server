package cn.edu.sdu.qd.oj.contest.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestSubmissionCreateReqDTO extends BaseDTO {

    @NotBlank
    private String language;

    @NotBlank
    private String code;

    @NotBlank
    private String problemCode;

    private Integer problemIndex;

    @NotNull
    private Long contestId;

    private String ipv4;

    private Long userId;
}
