package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.common.converter.BaseConvertUtils;
import cn.edu.sdu.qd.oj.problem.converter.ProblemTagConverter;
import cn.edu.sdu.qd.oj.problem.dao.ProblemTagDao;
import cn.edu.sdu.qd.oj.problem.dto.ProblemTagDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemManageListDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemTagDO;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProblemCommonService {

    @Autowired
    private ProblemTagDao problemTagDao;

    @Autowired
    private ProblemTagConverter problemTagConverter;



    public Map<Long, ProblemTagDTO> getTagDTOMapByProblemDOList(List<ProblemDO> problemDOList) {
        return getTagDTOMapByTagIdList(problemDOListToTagIdList(problemDOList));
    }

    public Map<Long, ProblemTagDTO> getTagDTOMapByProblemListDOList(List<ProblemManageListDO> problemManageListDOList) {
        return getTagDTOMapByTagIdList(problemListDOListToTagIdList(problemManageListDOList));
    }

    public List<ProblemTagDTO> getTagDTOListInProblemDOListFeature(List<ProblemDO> problemDOList) {
        return getTagDTOListByTagIdList(problemDOListToTagIdList(problemDOList));
    }

    public List<Long> problemListDOListToTagIdList(List<ProblemManageListDO> problemDOList) {
        return problemDOList.stream()
                .map(ProblemManageListDO::getFeatures)
                .filter(StringUtils::isNotBlank)
                .map(BaseConvertUtils::stringToMap)
                .map(map -> map.get("tags"))
                .map(tagsStr -> tagsStr.split(","))
                .flatMap(Arrays::stream)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    public List<Long> problemDOListToTagIdList(List<ProblemDO> problemDOList) {
        return problemDOList.stream()
                .map(ProblemDO::getFeatures)
                .filter(StringUtils::isNotBlank)
                .map(BaseConvertUtils::stringToMap)
                .map(map -> map.get("tags"))
                .map(tagsStr -> tagsStr.split(","))
                .flatMap(Arrays::stream)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    public Map<Long, ProblemTagDTO> getTagDTOMapByTagIdList(List<Long> tagIdList) {
        List<ProblemTagDTO> problemTagDTOList = getTagDTOListByTagIdList(tagIdList);
        return problemTagDTOList.stream().collect(Collectors.toMap(ProblemTagDTO::getId, Function.identity()));
    }

    public List<ProblemTagDTO> getTagDTOListByTagIdList(List<Long> tagIdList) {
        if (CollectionUtils.isEmpty(tagIdList)) {
            return Lists.newArrayList();
        }
        List<ProblemTagDO> problemTagDOList = problemTagDao.lambdaQuery().in(ProblemTagDO::getId, tagIdList).list();
        return problemTagConverter.to(problemTagDOList);
    }

    public List<Long> getTagIdListByFeatureMap(Map<String, String> featureMap) {
        return Optional.ofNullable(featureMap)
                       .map(map -> map.get("tags"))
                       .map(tagsStr -> tagsStr.split(","))
                       .map(tagArray -> Arrays.stream(tagArray)
                                              .map(Long::parseLong)
                                              .collect(Collectors.toList()))
                       .orElse(Lists.newArrayList());
    }
}
