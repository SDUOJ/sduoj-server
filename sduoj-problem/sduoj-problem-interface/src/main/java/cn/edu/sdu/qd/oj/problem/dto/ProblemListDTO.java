/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/4 21:46
 * @Version V1.0
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemListDTO extends BaseDTO {

    private Long problemId;

    private Map<String, String> features;

    private String problemCode;

    private String problemTitle;

    private String source;

    private String removeOj;

    private String removeUrl;

    private Integer submitNum;

    private Integer acceptNum;

    // ---------------------

    private List<ProblemTagDTO> problemTagDTOList;
}
