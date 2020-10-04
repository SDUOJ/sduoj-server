package cn.edu.sdu.qd.oj.submit.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubmissionListReqDTO extends BaseDTO {
    private int pageNow;
    private int pageSize;
    private String orderBy;
    private Boolean ascending = false;

    private String username;
    private String problemCode;
    private Long problemId;
    private Long userId;

    private String language;
    private Integer judgeResult;

    private List<String> problemCodeList;
}
