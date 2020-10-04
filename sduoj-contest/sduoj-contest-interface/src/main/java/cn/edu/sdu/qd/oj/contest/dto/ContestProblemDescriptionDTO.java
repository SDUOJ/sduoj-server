package cn.edu.sdu.qd.oj.contest.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestProblemDescriptionDTO extends BaseDTO {

    private String markdownDescription;

    private String htmlDescription;

    private String htmlInput;

    private String htmlOutput;

    private String htmlSampleInput;

    private String htmlSampleOutout;

    private String htmlHint;
}
