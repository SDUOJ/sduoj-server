/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/4 21:46
 * @Version V1.0
 **/

@Data
@Table(name = "oj_problems")
public class ProblemListDTO extends BaseDTO {

    @Id
    @Column(name = "p_id")
    private Integer problemId;

    @Column(name = "p_is_public")
    private Integer isPublic;

    @Column(name = "p_title")
    private String problemTitle;

    @Column(name = "p_submit_num")
    private Integer submitNum;

    @Column(name = "p_accept_num")
    private Integer acceptNum;
}
