/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.common.util.ProblemCacheUtils;
import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.RedisUtils;
import cn.edu.sdu.qd.oj.common.util.UserCacheUtils;
import cn.edu.sdu.qd.oj.problem.converter.*;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDao;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDescriptionDao;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDescriptionDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDescriptionDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemManageDO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemManageDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemManageListDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Slf4j
public class ProblemManageService {

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private ProblemDescriptionDao problemDescriptionDao;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserCacheUtils userCacheUtils;

    @Autowired
    private ProblemManageConverter problemManageConverter;

    @Autowired
    private ProblemManageListConverter problemManageListConverter;

    @Autowired
    private ProblemDescriptionConverter problemDescriptionConverter;

    @Autowired
    private ProblemCacheUtils problemCacheUtils;

    public ProblemManageDTO queryByCode(String problemCode) {
        ProblemDO problemManageDO = problemDao.lambdaQuery().eq(ProblemDO::getProblemCode, problemCode).one();
        ProblemManageDTO problemManageDTO = problemManageConverter.to(problemManageDO);
        problemManageDTO.setUsername(userCacheUtils.getUsername(problemManageDO.getUserId()));
        return problemManageDTO;
    }

    @Transactional
    public String createProblem(ProblemManageDTO problem) {
        problem.setProblemId(null);
        problem.setProblemCode(null);
        ProblemDO problemDO = problemManageConverter.from(problem);
        if (!problemDao.save(problemDO)) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        // TODO: 魔法值解决
        problemDO.setProblemCode("SDUOJ-" + problemDO.getProblemId());
        if (!problemDao.lambdaUpdate()
                .eq(ProblemDO::getProblemId, problemDO.getProblemId())
                .set(ProblemDO::getProblemCode, problemDO.getProblemCode())
                .update()) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        // 更新缓存
        redisUtils.hset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE,
                String.valueOf(problem.getProblemId()),
                problem.getProblemTitle());
        return problemDO.getProblemCode();
    }

    public PageResult<ProblemManageListDTO> queryProblemByPage(int pageNow, int pageSize) {
        Page<ProblemDO> pageResult = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getGmtCreate,
                ProblemDO::getGmtModified,
                ProblemDO::getProblemCode,
                ProblemDO::getIsPublic,
                ProblemDO::getUserId,
                ProblemDO::getProblemTitle,
                ProblemDO::getSource,
                ProblemDO::getSubmitNum,
                ProblemDO::getAcceptNum,
                ProblemDO::getCheckpointNum
        ).page(new Page<>(pageNow, pageSize));
        List<ProblemManageListDTO> problemManageListDTOlist = problemManageListConverter.to(pageResult.getRecords());
        if (problemManageListDTOlist != null) {
            problemManageListDTOlist.forEach(problemManageListDTO ->
                    problemManageListDTO.setUsername(userCacheUtils.getUsername(problemManageListDTO.getUserId())));
        }
        return new PageResult<>(pageResult.getPages(), problemManageListDTOlist);
    }

    public void update(ProblemManageDTO problem) {
        ProblemDO problemDO = problemManageConverter.from(problem);
        log.info("{} -> {}", problem, problemDO);
        if (!problemDao.lambdaUpdate().eq(ProblemDO::getProblemCode, problemDO.getProblemCode()).update(problemDO)) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        if (problemDO.getProblemTitle() != null) {
            redisUtils.hset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE,
                    String.valueOf(problem.getProblemId()),
                    problem.getProblemTitle());
        }
        if (problemDO.getCheckpointNum() != null) {
            redisUtils.hset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_CHECKPOINTNUM,
                    String.valueOf(problemDO.getProblemId()), problemDO.getCheckpointNum());
        }
    }

    public long createDescription(ProblemDescriptionDTO problemDescriptionDTO) {
        problemDescriptionDTO.setProblemId(problemCacheUtils.getProblemId(problemDescriptionDTO.getProblemCode()));

        ProblemDescriptionDO problemDescriptionDO = problemDescriptionConverter.from(problemDescriptionDTO);
        if (!problemDescriptionDao.save(problemDescriptionDO)) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        return problemDescriptionDO.getId();
    }
}