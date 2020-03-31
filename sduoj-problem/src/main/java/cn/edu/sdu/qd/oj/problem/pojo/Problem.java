package cn.edu.sdu.qd.oj.problem.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(name = "oj_problems")
public class Problem implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "p_id")
    private Integer problemId;

    @Column(name = "p_is_public")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Byte isPublic;

    @Column(name = "u_id")
    private Integer userId;

    @Column(name = "p_title")
    private String problemTitle;

    @Column(name = "p_submit_num")
    private Integer submitNum;

    @Column(name = "p_accept_num")
    private Integer acceptNum;

    @Column(name = "p_time_limit")
    private Integer timeLimit;

    @Column(name = "p_memory_limit")
    private Integer memoryLimit;

    @Column(name = "p_markdown")
    private String markdown;

}
