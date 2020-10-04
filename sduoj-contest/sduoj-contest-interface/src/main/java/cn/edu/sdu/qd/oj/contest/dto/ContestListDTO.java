/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.contest.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestListDTO extends BaseDTO {

    private Long contestId;

    private Date gmtCreate;

    private Date gmtModified;

    private Map<String, String> features;

    private String contestTitle;

    private Long userId;

    private Date gmtStart;

    private Date gmtEnd;

    private String source;

    private Integer participantNum;

    // -----------------

    private String username;
}