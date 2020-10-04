package cn.edu.sdu.qd.oj.contest.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestProblemDTO extends BaseDTO {

    // 脱敏后的含义是 problemIndex，脱敏前的含义是 problemCode
    private String problemCode;

    private Integer problemWeight;

    private String problemTitle;

    private List<String> languages;

    private Integer memoryLimit;

    private Integer timeLimit;

    // 其他字段，如在该比赛内的过题人数


    // ------------------------------------------

    private ContestProblemDescriptionDTO problemDescriptionDTO;
}
