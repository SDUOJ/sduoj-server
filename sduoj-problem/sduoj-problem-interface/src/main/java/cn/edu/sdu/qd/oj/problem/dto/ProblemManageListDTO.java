/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemManageListDTO extends BaseDTO {

    private Integer problemId;

    private Integer isPublic;

    private Integer userId;

    private String problemTitle;

    private Integer submitNum;

    private Integer acceptNum;

    private Integer timeLimit;

    private Integer memoryLimit;

    private Integer checkpointNum;
}