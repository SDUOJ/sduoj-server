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
public class ProblemManageListDTO extends BaseDTO {

    private Long problemId;

    private Date gmtCreate;

    private Date gmtModified;

    private Map<String, String> features;

    private String problemCode;

    private Integer isPublic;

    private Long userId;

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

    private Integer checkpointNum;

    // ------------------------------------------

    private List<ProblemTagDTO> problemTagDTOList;

    private String username;
}