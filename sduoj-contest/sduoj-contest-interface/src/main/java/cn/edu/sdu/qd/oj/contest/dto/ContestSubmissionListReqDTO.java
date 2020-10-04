package cn.edu.sdu.qd.oj.contest.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import cn.edu.sdu.qd.oj.common.util.HexStringToLongDeserializer;
import cn.edu.sdu.qd.oj.common.util.LongToHexStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestSubmissionListReqDTO extends BaseDTO {
    private int pageNow;
    private int pageSize;
    private String orderBy;
    private Boolean ascending = false;

    @NotNull
    private Long contestId;

    private String username;
    private String problemCode;
    private Integer problemIndex;
    private Long userId;

    private String language;
    private Integer judgeResult;
}