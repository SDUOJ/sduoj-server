/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.problem.entity.ProblemJudgerDO;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemJudgerDOMapper;
import cn.edu.sdu.qd.oj.problem.dto.ProblemJudgerDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName ProblemJudgerService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:33
 * @Version V1.0
 **/

@Service
public class ProblemJudgerService {
    @Autowired
    private ProblemJudgerDOMapper problemJudgerDOMapper;

    public ProblemJudgerDTO queryById(int problemId) {
        ProblemJudgerDO problemJudgerDO = this.problemJudgerDOMapper.selectByPrimaryKey(problemId);
        ProblemJudgerDTO problemJudgeDTO = new ProblemJudgerDTO();
        BeanUtils.copyProperties(problemJudgerDO, problemJudgeDTO);
        return problemJudgeDTO;
    }
}