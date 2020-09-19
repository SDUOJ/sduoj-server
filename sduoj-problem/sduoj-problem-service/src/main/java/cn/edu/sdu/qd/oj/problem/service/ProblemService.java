package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.RedisUtils;
import cn.edu.sdu.qd.oj.problem.converter.*;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDao;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;

import cn.edu.sdu.qd.oj.problem.dto.ProblemListDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProblemService {
    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ProblemConverter problemConverter;

    @Autowired
    private ProblemListConverter problemListConverter;

    public ProblemDTO queryById(Integer problemId) {
        ProblemDO problemDO = problemDao.getById(problemId);
        if (problemDO == null) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_FOUND);
        }
        if (problemDO.getIsPublic() == 0) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_PUBLIC);
        }
        return problemConverter.to(problemDO);
    }

    public PageResult<ProblemListDTO> queryProblemByPage(int pageNow, int pageSize) {
        Page<ProblemDO> pageResult = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getIsPublic,
                ProblemDO::getProblemTitle,
                ProblemDO::getSubmitNum,
                ProblemDO::getAcceptNum
        ).eq(ProblemDO::getIsPublic, 1).page(new Page<>(pageNow, pageSize));
        List<ProblemListDTO> problemListDTOList = problemListConverter.to(pageResult.getRecords());
        return new PageResult<>(pageResult.getPages(), problemListDTOList);
    }

    public Map<Integer, String> queryIdToTitleMap() {
        List<ProblemDO> problemDOList = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getProblemTitle
        ).list();
        return problemDOList.stream().collect(Collectors.toMap(ProblemDO::getUserId, ProblemDO::getUsername, (k1, k2) -> k1));
    }

    @PostConstruct
    private void initRedisProblemHash() {
        List<ProblemDO> problemDOList = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getProblemTitle,
                ProblemDO::getCheckpointNum
        ).list();
        Map<String, Object> ret1 = problemDOList.stream().collect(Collectors.toMap(problemDO -> problemDO.getProblemId().toString(), ProblemDO::getProblemTitle, (k1, k2) -> k1));
        Map<String, Object> ret2 = problemDOList.stream().collect(Collectors.toMap(problemDO -> problemDO.getProblemId().toString(), ProblemDO::getCheckpointNum, (k1, k2) -> k1));
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE, ret1);
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_CHECKPOINTNUM, ret2);
    }
}
