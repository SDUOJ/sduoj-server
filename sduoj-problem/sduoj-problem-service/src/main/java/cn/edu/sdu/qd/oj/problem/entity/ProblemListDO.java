/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(ProblemDOField.TABLE_NAME)
public class ProblemListDO extends BaseDO {

    @TableId(value = ProblemDOField.ID, type = IdType.AUTO)
    private Integer problemId;

    @TableField(ProblemDOField.IS_PUBLIC)
    private Integer isPublic;

    @TableField(ProblemDOField.TITLE)
    private String problemTitle;

    @TableField(ProblemDOField.SUBMIT_NUM)
    private Integer submitNum;

    @TableField(ProblemDOField.ACCEPT_NUM)
    private Integer acceptNum;
}
