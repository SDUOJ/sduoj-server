package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemMapper;
import cn.edu.sdu.qd.oj.problem.pojo.Problem;

import net.minidev.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.genid.GenId;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProblemService {
    @Autowired
    private ProblemMapper problemMapper;

    public Problem queryById(Integer id) {
        Problem problem = this.problemMapper.selectByPrimaryKey(id);
        if (problem == null) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_FOUND);
        }
        return problem;
    }
}
