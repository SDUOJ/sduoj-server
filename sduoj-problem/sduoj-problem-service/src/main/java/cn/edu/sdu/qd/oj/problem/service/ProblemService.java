package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.RedisUtils;
import cn.edu.sdu.qd.oj.common.util.UserCacheUtils;
import cn.edu.sdu.qd.oj.problem.converter.*;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDao;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDescriptionDao;
import cn.edu.sdu.qd.oj.problem.dao.ProblemTagDao;
import cn.edu.sdu.qd.oj.problem.dto.ProblemListReqDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemTagDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;

import cn.edu.sdu.qd.oj.problem.dto.ProblemListDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDOField;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDescriptionDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemTagDO;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProblemService {

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private ProblemDescriptionDao problemDescriptionDao;

    @Autowired
    private ProblemTagDao problemTagDao;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserCacheUtils userCacheUtils;

    @Autowired
    private ProblemConverter problemConverter;

    @Autowired
    private ProblemListConverter problemListConverter;

    @Autowired
    private ProblemTagConverter problemTagConverter;

    public ProblemDTO queryByCode(String problemCode) {
        ProblemDO problemDO = problemDao.lambdaQuery().select(
                ProblemDO.class, field -> !field.getColumn().equals(ProblemDOField.CHECKPOINTS)
        ).eq(ProblemDO::getProblemCode, problemCode).one();
        if (problemDO == null) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_FOUND);
        }
        if (problemDO.getIsPublic() == 0) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_PUBLIC);
        }
        ProblemDescriptionDO problemDescriptionDO = problemDescriptionDao.getById(problemDO.getDefaultDescriptionId());
        List<ProblemDescriptionDO> problemDescriptionDOList = problemDescriptionDao.lambdaQuery().select(
                ProblemDescriptionDO::getId,
                ProblemDescriptionDO::getProblemId,
                ProblemDescriptionDO::getUserId,
                ProblemDescriptionDO::getVoteNum
        ).eq(ProblemDescriptionDO::getProblemId, problemDO.getProblemId()).list();
        ProblemDTO problemDTO = problemConverter.to(problemDO, problemDescriptionDO, problemDescriptionDOList);

        // TODO: 考虑设计一个 annotation 和 cacheUtil 关联起来，自动填充一些业务字段
        try {
            problemDTO.getProblemDescriptionDTO().setProblemCode(problemCode);
            problemDTO.getProblemDescriptionListDTOList().forEach(o -> {
                o.setProblemCode(problemCode);
                o.setUsername(userCacheUtils.getUsername(o.getUserId()));
            });
        } catch (Exception ignore) {
        }

        // 置 tagDTO
        List<Long> tags = getTagIdListFromFeatureMap(problemDTO.getFeatures());
        if (!CollectionUtils.isEmpty(tags)) {
            List<ProblemTagDO> problemTagDOList = problemTagDao.lambdaQuery().in(ProblemTagDO::getId, tags).list();
            problemDTO.setProblemTagDTOList(problemTagConverter.to(problemTagDOList));
        }
        return problemDTO;
    }

    public PageResult<ProblemListDTO> queryProblemByPage(ProblemListReqDTO problemListReqDTO) {
        LambdaQueryChainWrapper<ProblemDO> query = problemDao.lambdaQuery();
        query.select(
                ProblemDO::getProblemId,
                ProblemDO::getFeatures,
                ProblemDO::getProblemCode,
                ProblemDO::getProblemTitle,
                ProblemDO::getSource,
                ProblemDO::getRemoteOj,
                ProblemDO::getRemoteUrl,
                ProblemDO::getSubmitNum,
                ProblemDO::getAcceptNum
        ).eq(ProblemDO::getIsPublic, 1);
        Optional.ofNullable(problemListReqDTO.getOrderBy()).ifPresent(orderBy -> {
            switch (problemListReqDTO.getOrderBy()) {
                case "acceptNum":
                    query.orderBy(true, problemListReqDTO.getAscending(), ProblemDO::getAcceptNum);
                    break;
                default:
                    break;
            }
        });
        Optional.ofNullable(problemListReqDTO.getRemoteOj()).ifPresent(remoteOj -> {
            query.eq(ProblemDO::getRemoteOj, remoteOj);
        });
        Page<ProblemDO> pageResult = query.page(new Page<>(problemListReqDTO.getPageNow(), problemListReqDTO.getPageSize()));
        List<ProblemListDTO> problemListDTOList = problemListConverter.to(pageResult.getRecords());

        // 取 tag TODO: 魔法值解决
        List<Long> tags = problemListDTOList.stream()
                .map(ProblemListDTO::getFeatures)
                .filter(Objects::nonNull)
                .map(map -> map.get("tags"))
                .map(tagsStr -> tagsStr.split(","))
                .flatMap(Arrays::stream)
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<ProblemTagDO> problemTagDOList = problemTagDao.lambdaQuery().in(ProblemTagDO::getId, tags).list();
        List<ProblemTagDTO> problemTagDTOList = problemTagConverter.to(problemTagDOList);
        Map<Long, ProblemTagDTO> tagIdToProblemTagDTOMap = problemTagDTOList.stream().collect(Collectors.toMap(ProblemTagDTO::getId, Function.identity()));
        problemListDTOList.forEach(o -> o.setProblemTagDTOList(
                Optional.ofNullable(o.getFeatures())
                        .map(map -> map.get("tags"))
                        .map(tagsStr -> tagsStr.split(","))
                        .map(tagArray -> Arrays.stream(tagArray)
                                .map(Long::parseLong)
                                .map(tagIdToProblemTagDTOMap::get)
                                .collect(Collectors.toList()))
                        .orElse(null)
        ));
        return new PageResult<>(pageResult.getPages(), problemListDTOList);
    }

    private List<ProblemTagDO> getTagDTOListFromFeatures(String features) {
        if (StringUtils.isBlank(features)) {
            return null;
        }
        Map<String, String> featureMap = Arrays.stream(features.split(";")).collect(Collectors.toMap(s -> s.substring(0, s.indexOf(":")), s -> s.substring(s.indexOf(":") + 1), (k1, k2) -> k1));
        List<Long> tags = getTagIdListFromFeatureMap(featureMap);
        List<ProblemTagDO> problemTagDOList = problemTagDao.lambdaQuery().in(ProblemTagDO::getId, tags).list();
        return problemTagDOList;
    }

    private List<Long> getTagIdListFromFeatureMap(Map<String, String> featureMap) {
        List<Long> tags = Optional.ofNullable(featureMap)
                .map(map -> map.get("tags"))
                .map(tagsStr -> tagsStr.split(","))
                .map(tagArray -> Arrays.stream(tagArray)
                        .map(Long::parseLong)
                        .collect(Collectors.toList()))
                .orElse(null);
        return tags;
    }

    public Map<Long, String> queryIdToTitleMap() {
        List<ProblemDO> problemDOList = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getProblemTitle
        ).list();
        return problemDOList.stream().collect(Collectors.toMap(ProblemDO::getProblemId, ProblemDO::getProblemTitle, (k1, k2) -> k1));
    }

    @PostConstruct
    private void initRedisProblemHash() {
        List<ProblemDO> problemDOList = problemDao.lambdaQuery().select(
                ProblemDO::getProblemCode,
                ProblemDO::getProblemId,
                ProblemDO::getProblemTitle,
                ProblemDO::getCheckpointNum
        ).list();
        Map<String, Object> problemIdToTitle = problemDOList.stream().collect(Collectors.toMap(problemDO -> problemDO.getProblemId().toString(), ProblemDO::getProblemTitle, (k1, k2) -> k1));
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE, problemIdToTitle);
        Map<String, Object> problemIdToCheckpointNum = problemDOList.stream().collect(Collectors.toMap(problemDO -> problemDO.getProblemId().toString(), ProblemDO::getCheckpointNum, (k1, k2) -> k1));
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_CHECKPOINTNUM, problemIdToCheckpointNum);
        Map<String, Object> problemCodeToProblemId = problemDOList.stream().collect(Collectors.toMap(ProblemDO::getProblemCode, ProblemDO::getProblemId, (k1, k2) -> k1));
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_PROBLEM_CODE_TO_PROBLEM_ID, problemCodeToProblemId);
        Map<String, Object> problemIdToProblemCode = problemDOList.stream().collect(Collectors.toMap(problemDO -> problemDO.getProblemId().toString(), ProblemDO::getProblemCode, (k1, k2) -> k1));
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_PROBLEM_CODE, problemIdToProblemCode);
    }

    public boolean validateProblemCodeList(List<String> problemCodeList) {
        return problemCodeList.size() == problemDao.lambdaQuery().in(ProblemDO::getProblemCode, problemCodeList).count();
    }

    public ProblemDTO queryWithDescriptionId(String problemCode, long problemDescriptionId, long userId) {
        ProblemDO problemDO = problemDao.lambdaQuery().eq(ProblemDO::getProblemCode, problemCode).one();
        if (problemDO == null) {
            return null;
        }
        if (0 == problemDO.getIsPublic() && userId != problemDO.getUserId()) {
            return null;
        }
        ProblemDescriptionDO problemDescriptionDO = problemDescriptionDao.lambdaQuery()
                .eq(ProblemDescriptionDO::getProblemId, problemDO.getProblemId())
                .eq(ProblemDescriptionDO::getId, problemDescriptionId).one();
        return problemConverter.to(problemDO, problemDescriptionDO, null);
    }
}
