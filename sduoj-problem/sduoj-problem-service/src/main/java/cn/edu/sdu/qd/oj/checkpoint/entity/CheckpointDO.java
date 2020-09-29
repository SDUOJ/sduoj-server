/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.checkpoint.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

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
@TableName(CheckpointDOField.TABLE_NAME)
public class CheckpointDO extends BaseDO {
    public static final int MAX_DESCRIPTIONs_LENGTH = 32;

    @TableId(value = CheckpointDOField.ID, type = IdType.NONE)
    private Long checkpointId;

    @TableField(CheckpointDOField.INPUT_DESC)
    private String inputDescription;

    @TableField(CheckpointDOField.OUTPUT_DESC)
    private String outputDescription;

    @TableField(CheckpointDOField.INPUT_SIZE)
    private Integer inputSize;

    @TableField(CheckpointDOField.OUTPUT_SIZE)
    private Integer outputSize;

    @TableField(CheckpointDOField.INPUT_FILE_NAME)
    private String inputFileName;

    @TableField(CheckpointDOField.OUTPUT_FILE_NAME)
    private String outputFileName;

    public CheckpointDO(Long checkpointId, String inputDescription, String outputDescription, Integer inputSize, Integer outputSize) {
        this.checkpointId = checkpointId;
        this.inputDescription = inputDescription;
        this.outputDescription = outputDescription;
        this.inputSize = inputSize;
        this.outputSize = outputSize;
    }
}