/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @ClassName ProblemListBo
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/4 21:46
 * @Version V1.0
 **/

@Data
@Table(name = "oj_problems")
public class ProblemListBo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "p_id")
    private Integer id;

    @Column(name = "p_is_public")
    @JsonIgnore
    private Integer isPublic;

    @Column(name = "p_name")
    private String problemName;

    @Column(name = "p_submit_num")
    private Integer submitNum;

    @Column(name = "p_accept_num")
    private Integer acceptNum;
}
