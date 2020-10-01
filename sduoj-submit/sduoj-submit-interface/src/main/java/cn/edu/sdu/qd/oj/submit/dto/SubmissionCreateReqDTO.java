package cn.edu.sdu.qd.oj.submit.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubmissionCreateReqDTO extends BaseDTO {

    @NotBlank
    private String language;

    @NotBlank
    private String code;

    @NotBlank
    private String problemCode;

    private String ipv4;

    private Long userId;
}
