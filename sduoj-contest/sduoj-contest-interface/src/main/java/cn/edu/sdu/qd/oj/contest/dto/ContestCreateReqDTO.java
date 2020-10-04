/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.contest.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestCreateReqDTO extends BaseDTO {

    private Map<String, String> features;

    @Length(max = 60, message = "标题最大长度为 60")
    @NotBlank
    @NotNull
    private String contestTitle;

    private Long userId;

    private Date gmtStart;

    private Date gmtEnd;

    @Length(max = 60, message = "密码最大长度为 60")
    private String password;

    private String source;

    private String markdownDescription;

    @Size(max = 96, message = "最多出 96 道题")
    private List<ContestProblemListDTO> problems;

    private List<String> participants; // List<username>
}