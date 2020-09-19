/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.problem.converter.ProblemJudgerConverter;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDao;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
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
    private ProblemDao problemDao;

    @Autowired
    private ProblemJudgerConverter problemJudgerConverter;

    public ProblemJudgerDTO queryById(int problemId) {
        ProblemDO problemJudgerDO = problemDao.lambdaQuery().select(
            ProblemDO::getProblemId,
            ProblemDO::getIsPublic,
            ProblemDO::getTimeLimit,
            ProblemDO::getMemoryLimit,
            ProblemDO::getCheckpointNum,
            ProblemDO::getCheckpointIds
        ).eq(ProblemDO::getProblemId, problemId).one();
        return problemJudgerConverter.to(problemJudgerDO);
    }
}