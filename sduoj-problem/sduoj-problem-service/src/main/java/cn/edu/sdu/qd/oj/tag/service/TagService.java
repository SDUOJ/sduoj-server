/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.tag.service;


import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.tag.converter.TagConverter;
import cn.edu.sdu.qd.oj.tag.dao.TagDao;
import cn.edu.sdu.qd.oj.tag.dto.TagDTO;
import cn.edu.sdu.qd.oj.tag.entity.TagDO;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    @Autowired
    private TagDao tagDao;

    @Autowired
    private TagConverter tagConverter;

    public Long create(long parentId, String title) {
        TagDO tagDO = TagDO.builder().parentId(parentId).title(title).build();
        if (!tagDao.save(tagDO)) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        return tagDO.getId();
    }

    public List<TagDTO> listByParentId(Long parentId) {
        List<TagDO> tagDOList = tagDao.lambdaQuery().eq(TagDO::getParentId, parentId).list();
        List<TagDTO> tagDTOList = tagConverter.to(tagDOList);
        return Optional.ofNullable(tagDTOList).orElse(Lists.newArrayList());
    }

    public List<TagDTO> listByTitle(String title) {
        List<TagDO> tagDOList = tagDao.lambdaQuery().likeRight(TagDO::getTitle, title).list();
        List<TagDTO> tagDTOList = tagConverter.to(tagDOList);
        return Optional.ofNullable(tagDTOList).orElse(Lists.newArrayList());
    }


    public void update(TagDTO tagDTO) {
        TagDO tagDO = tagConverter.from(tagDTO);
        if (!tagDao.updateById(tagDO)) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
    }
}