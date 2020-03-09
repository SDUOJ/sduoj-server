package cn.edu.sdu.qd.oj.problem.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Integer id;

    @Column(name = "p_is_public")
    private Integer isPublic;

    @Column(name = "u_id")
    private Integer userId;

    @Column(name = "p_name")
    private String problemName;

    @Column(name = "p_submit_num")
    private Integer submitNum;

    @Column(name = "p_accept_num")
    private Integer acceptNum;

    @Column(name = "p_time_limit")
    private Integer timeLimit;

    @Column(name = "p_memory_limit")
    private Integer memoryLimit;

    @Column(name = "p_description")
    private String description;

    @Column(name = "p_input_format")
    private String inputFormat;

    @Column(name = "p_output_format")
    private String outputFormat;

    @Column(name = "p_sample_input")
    private String sampleInput;

    @Column(name = "p_sample_output")
    private String sampleOutput;

    @Column(name = "p_hint")
    private String hint;

}
