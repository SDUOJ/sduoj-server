package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.common.config.RedisConstants;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.utils.RedisUtils;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemListBoMapper;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemMapper;
import cn.edu.sdu.qd.oj.problem.pojo.Problem;

import cn.edu.sdu.qd.oj.problem.pojo.ProblemListBo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProblemService {
    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private ProblemListBoMapper problemListBoMapper;

    @Autowired
    private RedisUtils redisUtils;

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

    public Map<Integer, String> queryIdToTitleMap() {
        List<Map> list = problemMapper.queryIdToRedisHash();
        Map<Integer, String> ret = new HashMap<>(list.size());
        // TODO: 魔法值解决
        list.stream().forEach(map -> ret.put((Integer)map.get("p_id"), (String)map.get("p_title")));
        return ret;
    }

    @PostConstruct
    private void initRedisProblemHash() {
        List<Map> list = problemMapper.queryIdToRedisHash();
        Map<String, Object> ret1 = new HashMap<>(list.size());
        Map<String, Object> ret2 = new HashMap<>(list.size());
        // TODO: 魔法值解决
        list.stream().forEach(map -> {
            ret1.put(String.valueOf(map.get("p_id")), map.get("p_title"));
            ret2.put(String.valueOf(map.get("p_id")), map.get("p_checkpoint_num"));
        });
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE, ret1);
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_CHECKPOINTNUM, ret2);
    }
}
