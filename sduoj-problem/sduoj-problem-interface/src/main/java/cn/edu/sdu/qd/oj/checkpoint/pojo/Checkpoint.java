/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.checkpoint.pojo;

import cn.edu.sdu.qd.oj.common.utils.HexStringToLongDeserializer;
import cn.edu.sdu.qd.oj.common.utils.LongToHexStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @ClassName checkpoint
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:23
 * @Version V1.0
 **/

@Data
@AllArgsConstructor
@Table(name = "oj_checkpoints")
public class Checkpoint implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int MAX_DESCRIPTION_LENGTH = 32;

    @Id
    @Column(name = "c_id")
    @JsonSerialize(using = LongToHexStringSerializer.class)
    @JsonDeserialize(using = HexStringToLongDeserializer.class)
    private Long checkpointId;

    @Column(name = "c_input_desc")
    private String inputDescription;

    @Column(name = "c_output_desc")
    private String outputDescription;

    @Column(name = "c_input_size")
    private Integer inputSize;

    @Column(name = "c_output_size")
    private Integer outputSize;

    @Column(name = "c_input_file_name")
    private String inputFileName;

    @Column(name = "c_output_file_name")
    private String outputFileName;

    public Checkpoint(Long checkpointId, String inputDescription, String outputDescription, Integer inputSize, Integer outputSize) {
        this.checkpointId = checkpointId;
        this.inputDescription = inputDescription;
        this.outputDescription = outputDescription;
        this.inputSize = inputSize;
        this.outputSize = outputSize;
    }
}