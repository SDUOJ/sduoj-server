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
import cn.edu.sdu.qd.oj.common.util.UserCacheUtils;
import cn.edu.sdu.qd.oj.problem.converter.ProblemConverter;
import cn.edu.sdu.qd.oj.problem.converter.ProblemListConverter;
import cn.edu.sdu.qd.oj.problem.converter.ProblemManageConverter;
import cn.edu.sdu.qd.oj.problem.converter.ProblemManageListConverter;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDao;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemManageDO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemManageDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemManageListDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ProblemDao problemDao;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserCacheUtils userCacheUtils;

    @Autowired
    private ProblemManageConverter problemManageConverter;

    @Autowired
    private ProblemManageListConverter problemManageListConverter;

    public ProblemManageDTO queryById(Integer problemId) {
        ProblemDO problemManageDO = problemDao.lambdaQuery().select(
            ProblemDO::getProblemId,
            ProblemDO::getIsPublic,
            ProblemDO::getUserId,
            ProblemDO::getProblemTitle,
            ProblemDO::getTimeLimit,
            ProblemDO::getMemoryLimit,
            ProblemDO::getMarkdown,
            ProblemDO::getCheckpointNum,
            ProblemDO::getCheckpointIds
        ).eq(ProblemDO::getProblemId, problemId).one();
        problemManageDO.setUsername(userCacheUtils.getUsername(problemManageDO.getUserId()));
        return problemManageConverter.to(problemManageDO);
    }

    public boolean createProblem(ProblemManageDTO problem) {
        problem.setProblemId(null);
        ProblemDO problemDO = problemManageConverter.from(problem);
        if (!problemDao.save(problemDO)) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        // 更新缓存
        redisUtils.hset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE,
                String.valueOf(problem.getProblemId()),
                problem.getProblemTitle());
        return true;
    }

    public PageResult<ProblemManageListDTO> queryProblemByPage(int pageNow, int pageSize) {
        Page<ProblemDO> pageResult = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getIsPublic,
                ProblemDO::getUserId,
                ProblemDO::getProblemTitle,
                ProblemDO::getSubmitNum,
                ProblemDO::getAcceptNum,
                ProblemDO::getTimeLimit,
                ProblemDO::getMemoryLimit,
                ProblemDO::getCheckpointNum
        ).page(new Page<>(pageNow, pageSize));
        List<ProblemManageListDTO> problemManageListDTOlist = problemManageListConverter.to(pageResult.getRecords());
        return new PageResult<>(pageResult.getPages(), problemManageListDTOlist);
    }

    public void update(ProblemManageDTO problem) {
        ProblemDO problemDO = problemManageConverter.from(problem);
        if (!problemDao.updateById(problemDO))
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        if (problemDO.getProblemTitle() != null) {
            redisUtils.hset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE,
                    String.valueOf(problem.getProblemId()),
                    problem.getProblemTitle());
        }
        if (problemDO.getCheckpointNum() != null) {
            redisUtils.hset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_CHECKPOINTNUM, String.valueOf(problemDO.getProblemId()), problemDO.getCheckpointNum());
        }
    }
}