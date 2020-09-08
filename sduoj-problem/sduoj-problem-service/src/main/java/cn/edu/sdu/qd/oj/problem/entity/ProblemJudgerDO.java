/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = ProblemDOField.TABLE_NAME)
public class ProblemJudgerDO extends BaseDO {

    @Id
    @Column(name = ProblemDOField.ID)
    private Integer problemId;

    @Column(name = ProblemDOField.IS_PUBLIC)
    private Integer isPublic;

    @Column(name = ProblemDOField.TIME_LIMIT)
    private Integer timeLimit;

    @Column(name = ProblemDOField.MEMORY_LIMIT)
    private Integer memoryLimit;

    @Column(name = ProblemDOField.CHECKPOINT_NUM)
    private Integer checkpointNum;

    @Column(name = ProblemDOField.CHECKPOINT_IDS)
    private byte[] checkpointIds;
}
