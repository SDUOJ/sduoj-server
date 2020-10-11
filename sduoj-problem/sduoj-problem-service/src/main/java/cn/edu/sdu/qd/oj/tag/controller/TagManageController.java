/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.tag.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.tag.dto.TagDTO;
import cn.edu.sdu.qd.oj.tag.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/manage/tag")
public class TagManageController {

    @Autowired
    private TagService tagService;

    @PostMapping("/create")
    @ApiResponseBody
    public Long createTag(@RequestBody Map<String, String> json) {
        // 提取入参
        long parentId = Long.parseLong(json.get("parentId"));
        String title = json.get("title");
        return tagService.create(parentId, title);
    }

    @PostMapping("/update")
    @ApiResponseBody
    public Void updateTag(@RequestBody TagDTO tagDTO) {
        tagService.update(tagDTO);
        return null;
    }

    @GetMapping("/list")
    @ApiResponseBody
    public List<TagDTO> list(TagDTO tagDTO) {
        if (tagDTO.getParentId() != null) {
            return tagService.listByParentId(tagDTO.getParentId());
        }
        if (tagDTO.getTitle() != null) {
            return tagService.listByTitle(tagDTO.getTitle());
        }
        return Lists.newArrayList();
    }

}
