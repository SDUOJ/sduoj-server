/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.problem.mapper.ProblemJudgerBoMapper;
import cn.edu.sdu.qd.oj.problem.pojo.ProblemJudgerBo;
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
    private ProblemJudgerBoMapper problemJudgerBoMapper;

    public ProblemJudgerBo queryById(int problemId) {
        return this.problemJudgerBoMapper.selectByPrimaryKey(problemId);
    }
}