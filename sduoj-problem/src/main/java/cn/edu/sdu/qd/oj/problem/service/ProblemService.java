package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemJudgerBoMapper;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemListBoMapper;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemManageBoMapper;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemMapper;
import cn.edu.sdu.qd.oj.problem.pojo.Problem;

import cn.edu.sdu.qd.oj.problem.pojo.ProblemJudgerBo;
import cn.edu.sdu.qd.oj.problem.pojo.ProblemListBo;
import cn.edu.sdu.qd.oj.problem.pojo.ProblemManageBo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import net.minidev.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.genid.GenId;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProblemService {
    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private ProblemListBoMapper problemListBoMapper;

    @Autowired
    private ProblemJudgerBoMapper problemJudgerBoMapper;

    @Autowired
    private ProblemManageBoMapper problemManageBoMapper;

    public Problem queryById(Integer problemId) {
        Problem problem = this.problemMapper.selectByPrimaryKey(problemId);
        if (problem == null) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_FOUND);
        }
        if (problem.getIsPublic() == 0) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_PUBLIC);
        }
        return problem;
    }

    public PageResult<ProblemListBo> queryProblemByPage(int pageNow, int pageSize) {
        PageHelper.startPage(pageNow, pageSize);
        Example example = new Example(ProblemListBo.class);
        example.createCriteria().andEqualTo("isPublic", 1);
        Page<ProblemListBo> pageInfo = (Page<ProblemListBo>) problemListBoMapper.selectByExample(example);
        return new PageResult<>(pageInfo.getPages(), pageInfo);
    }

    public ProblemJudgerBo queryByJudger(int problemId) {
        return this.problemJudgerBoMapper.selectByPrimaryKey(problemId);
    }

    public ProblemManageBo queryManageBoById(Integer problemId) {
        return this.problemManageBoMapper.selectByPrimaryKey(problemId);
    }
}
