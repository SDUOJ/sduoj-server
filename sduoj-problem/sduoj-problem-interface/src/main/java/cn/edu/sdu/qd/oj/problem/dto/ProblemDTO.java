package cn.edu.sdu.qd.oj.problem.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemDTO extends BaseDTO {

    private Long problemId;

    private Map<String, String> features;

    private String problemCode;

    private String problemTitle;

    private String source;

    private String remoteOj;

    private String remoteUrl;

    private Integer submitNum;

    private Integer acceptNum;

    private List<String> languages;

    private Integer memoryLimit;

    private Integer timeLimit;

    private Long defaultDescriptionId;

    // ------------------------------------------

    private ProblemDescriptionDTO problemDescriptionDTO;

    private List<ProblemDescriptionListDTO> problemDescriptionListDTOList;

    private List<ProblemTagDTO> problemTagDTOList;
}
