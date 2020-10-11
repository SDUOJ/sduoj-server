/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.common.converter.BaseConvertUtils;
import cn.edu.sdu.qd.oj.tag.converter.TagConverter;
import cn.edu.sdu.qd.oj.tag.dao.TagDao;
import cn.edu.sdu.qd.oj.tag.dto.TagDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemManageListDO;
import cn.edu.sdu.qd.oj.tag.entity.TagDO;
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
    private TagDao tagDao;

    @Autowired
    private TagConverter tagConverter;



    public Map<Long, TagDTO> getTagDTOMapByProblemDOList(List<ProblemDO> problemDOList) {
        return getTagDTOMapByTagIdList(problemDOListToTagIdList(problemDOList));
    }

    public Map<Long, TagDTO> getTagDTOMapByProblemListDOList(List<ProblemManageListDO> problemManageListDOList) {
        return getTagDTOMapByTagIdList(problemListDOListToTagIdList(problemManageListDOList));
    }

    public List<TagDTO> getTagDTOListInProblemDOListFeature(List<ProblemDO> problemDOList) {
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

    public Map<Long, TagDTO> getTagDTOMapByTagIdList(List<Long> tagIdList) {
        List<TagDTO> tagDTOList = getTagDTOListByTagIdList(tagIdList);
        return tagDTOList.stream().collect(Collectors.toMap(TagDTO::getId, Function.identity()));
    }

    public List<TagDTO> getTagDTOListByTagIdList(List<Long> tagIdList) {
        if (CollectionUtils.isEmpty(tagIdList)) {
            return Lists.newArrayList();
        }
        List<TagDO> tagDOList = tagDao.lambdaQuery().in(TagDO::getId, tagIdList).list();
        return tagConverter.to(tagDOList);
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