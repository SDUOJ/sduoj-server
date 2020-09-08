/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.checkpoint.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author zhangt2333
 * @Date 2020/9/8 10:30
 * @Version V1.0
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = CheckpointDOField.TABLE_NAME)
public class CheckpointDO extends BaseDO {
    public static final int MAX_DESCRIPTIONs_LENGTH = 32;

    @Id
    @Column(name = CheckpointDOField.ID)
    private Long checkpointId;

    @Column(name = CheckpointDOField.INPUT_DESC)
    private String inputDescription;

    @Column(name = CheckpointDOField.OUTPUT_DESC)
    private String outputDescription;

    @Column(name = CheckpointDOField.INPUT_SIZE)
    private Integer inputSize;

    @Column(name = CheckpointDOField.OUTPUT_SIZE)
    private Integer outputSize;

    @Column(name = CheckpointDOField.INPUT_FILE_NAME)
    private String inputFileName;

    @Column(name = CheckpointDOField.OUTPUT_FILE_NAME)
    private String outputFileName;

    public CheckpointDO(Long checkpointId, String inputDescription, String outputDescription, Integer inputSize, Integer outputSize) {
        this.checkpointId = checkpointId;
        this.inputDescription = inputDescription;
        this.outputDescription = outputDescription;
        this.inputSize = inputSize;
        this.outputSize = outputSize;
    }
}