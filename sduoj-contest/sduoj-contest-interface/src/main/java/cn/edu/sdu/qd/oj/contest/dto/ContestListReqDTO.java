package cn.edu.sdu.qd.oj.contest.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestListReqDTO extends BaseDTO {
    private int pageNow;
    private int pageSize;
    private String orderBy;
    private Boolean ascending = false;


}