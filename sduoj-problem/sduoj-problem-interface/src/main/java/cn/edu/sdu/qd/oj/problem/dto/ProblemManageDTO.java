/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import cn.edu.sdu.qd.oj.problem.utils.BytesToCheckpointIdsSerializer;
import cn.edu.sdu.qd.oj.problem.utils.CheckpointIdsToBytesDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemManageDTO extends BaseDTO {

    private Integer problemId;

    private Integer isPublic;

    private Integer userId;

    private String problemTitle;

    private Integer timeLimit;

    private Integer memoryLimit;

    private String markdown;

    private Integer checkpointNum;

    @JsonSerialize(using = BytesToCheckpointIdsSerializer.class)
    @JsonDeserialize(using = CheckpointIdsToBytesDeserializer.class)
    private byte[] checkpointIds;

    private String username;
}