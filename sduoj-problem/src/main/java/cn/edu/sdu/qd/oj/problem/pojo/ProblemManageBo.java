/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.pojo;

import cn.edu.sdu.qd.oj.problem.utils.BytesToCheckpointIdsSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @ClassName ProblemManageBo
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/1 19:51
 * @Version V1.0
 **/

@Data
@Table(name = "oj_problems")
public class ProblemManageBo extends Problem {

    @Column(name = "p_checkpoint_ids")
    @JsonSerialize(using = BytesToCheckpointIdsSerializer.class)
    private byte[] checkpointIds;
}