package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.common.config.RedisConstants;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.utils.RedisUtils;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDOField;
import cn.edu.sdu.qd.oj.problem.entity.ProblemListDO;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemListDOMapper;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemDOMapper;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;

import cn.edu.sdu.qd.oj.problem.dto.ProblemListDTO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProblemService {
    @Autowired
    private ProblemDOMapper problemDOMapper;

    @Autowired
    private ProblemListDOMapper problemListDOMapper;

    @Autowired
    private RedisUtils redisUtils;

    public ProblemDTO queryById(Integer problemId) {
        ProblemDO problemDO = this.problemDOMapper.selectByPrimaryKey(problemId);
        if (problemDO == null) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_FOUND);
        }
        if (problemDO.getIsPublic() == 0) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_PUBLIC);
        }
        ProblemDTO problemDTO = new ProblemDTO();
        BeanUtils.copyProperties(problemDO, problemDTO);
        return problemDTO;
    }

    public PageResult<ProblemListDTO> queryProblemByPage(int pageNow, int pageSize) {
        PageHelper.startPage(pageNow, pageSize);
        Example example = new Example(ProblemListDO.class);
        example.createCriteria().andEqualTo("isPublic", 1);
        Page<ProblemListDO> pageInfo = (Page<ProblemListDO>) problemListDOMapper.selectByExample(example);
        List<ProblemListDTO> problemListDTOList = pageInfo.stream().map(problemListDO -> {
            ProblemListDTO problemListDTO = new ProblemListDTO();
            BeanUtils.copyProperties(problemListDO, problemListDTO);
            return problemListDTO;
        }).collect(Collectors.toList());
        return new PageResult<>(pageInfo.getPages(), problemListDTOList);
    }

    public Map<Integer, String> queryIdToTitleMap() {
        List<Map> list = problemDOMapper.queryIdToRedisHash();
        Map<Integer, String> ret = new HashMap<>(list.size());
        list.stream().forEach(map -> ret.put((Integer)map.get(ProblemDOField.ID), (String)map.get(ProblemDOField.TITLE)));
        return ret;
    }

    @PostConstruct
    private void initRedisProblemHash() {
        List<Map> list = problemDOMapper.queryIdToRedisHash();
        Map<String, Object> ret1 = new HashMap<>(list.size());
        Map<String, Object> ret2 = new HashMap<>(list.size());
        list.stream().forEach(map -> {
            ret1.put(String.valueOf(map.get(ProblemDOField.ID)), map.get(ProblemDOField.TITLE));
            ret2.put(String.valueOf(map.get(ProblemDOField.ID)), map.get(ProblemDOField.CHECKPOINT_NUM));
        });
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE, ret1);
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_CHECKPOINTNUM, ret2);
    }
}
