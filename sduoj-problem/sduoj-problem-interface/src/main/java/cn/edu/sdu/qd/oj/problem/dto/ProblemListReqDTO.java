package cn.edu.sdu.qd.oj.problem.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemListReqDTO extends BaseDTO {
    private int pageNow;
    private int pageSize;
    private String orderBy;
    private Boolean ascending = false;
    private String remoteOj;
}
