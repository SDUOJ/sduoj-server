/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import cn.edu.sdu.qd.oj.problem.utils.BytesToCheckpointIdsSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemJudgerDTO extends BaseDTO {

    private Integer problemId;

    private Integer isPublic;

    private Integer timeLimit;

    private Integer memoryLimit;

    private Integer checkpointNum;

    @JsonSerialize(using = BytesToCheckpointIdsSerializer.class)
    private byte[] checkpointIds;
}
