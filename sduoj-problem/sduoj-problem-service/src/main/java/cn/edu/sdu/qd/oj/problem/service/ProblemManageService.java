/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.RedisUtils;
import cn.edu.sdu.qd.oj.problem.entity.ProblemManageDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemManageListDO;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemManageDOMapper;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemManageListDOMapper;
import cn.edu.sdu.qd.oj.problem.dto.ProblemManageDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemManageListDTO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName ProblemManageService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:31
 * @Version V1.0
 **/

@Service
public class ProblemManageService {
    @Autowired
    private ProblemManageDOMapper problemManageDOMapper;

    @Autowired
    private ProblemManageListDOMapper problemManageListDOMapper;

    @Autowired
    private RedisUtils redisUtils;

    public ProblemManageDTO queryById(Integer problemId) {
        ProblemManageDO problemManageDO = this.problemManageDOMapper.selectByPrimaryKey(problemId);
        ProblemManageDTO problemManageDTO = new ProblemManageDTO();
        BeanUtils.copyProperties(problemManageDO, problemManageDTO);
        return problemManageDTO;
    }

    public boolean createProblem(ProblemManageDTO problem) {
        ProblemManageDO problemManageDO = new ProblemManageDO();
        BeanUtils.copyProperties(problem, problemManageDO);
        problemManageDO.setProblemId(null);

        if (this.problemManageDOMapper.insertSelective(problemManageDO) != 1) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        // 更新缓存
        redisUtils.hset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE,
                String.valueOf(problem.getProblemId()),
                problem.getProblemTitle());
        return true;
    }

    public PageResult<ProblemManageListDTO> queryProblemByPage(int pageNow, int pageSize) {
        Example example = new Example(ProblemManageListDO.class);
        PageHelper.startPage(pageNow, pageSize);
        Page<ProblemManageListDO> pageInfo = (Page<ProblemManageListDO>) problemManageListDOMapper.selectByExample(example);
        List<ProblemManageListDTO> problemManageListDTOlist = pageInfo.stream().map(problemManageListDO -> {
            ProblemManageListDTO problemManageListDTO = new ProblemManageListDTO();
            BeanUtils.copyProperties(problemManageListDO, problemManageListDTO);
            return problemManageListDTO;
        }).collect(Collectors.toList());
        return new PageResult<>(pageInfo.getPages(), problemManageListDTOlist);
    }

    public void update(ProblemManageDTO problem) {
        ProblemManageDO problemManageDO = new ProblemManageDO();
        BeanUtils.copyProperties(problem, problemManageDO);

        if (this.problemManageDOMapper.updateByPrimaryKeySelective(problemManageDO) != 1)
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        if (problem.getProblemTitle() != null) {
            redisUtils.hset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE,
                    String.valueOf(problem.getProblemId()),
                    problem.getProblemTitle());
        }
        if (problem.getCheckpointNum() != null) {
            redisUtils.hset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_CHECKPOINTNUM, String.valueOf(problem.getProblemId()), problem.getCheckpointNum());
        }
    }
}