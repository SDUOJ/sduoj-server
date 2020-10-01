/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import cn.edu.sdu.qd.oj.problem.util.BytesToCheckpointIdsSerializer;
import cn.edu.sdu.qd.oj.problem.util.CheckpointIdsToBytesDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemManageDTO extends BaseDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long problemId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date gmtCreate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date gmtModified;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Map<String, String> features;

    private String problemCode;

    private Integer isPublic;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;

    private String problemTitle;

    private String source;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String removeOj;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String removeUrl;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer submitNum;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer acceptNum;

    private List<String> languages;

    private Integer memoryLimit;

    private Integer timeLimit;

    private Long defaultDescriptionId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer checkpointNum;

    @JsonSerialize(using = BytesToCheckpointIdsSerializer.class)
    @JsonDeserialize(using = CheckpointIdsToBytesDeserializer.class)
    private byte[] checkpoints;

    // ------------------------------------------

    private ProblemDescriptionDTO problemDescriptionDTO;

    private String username;
}