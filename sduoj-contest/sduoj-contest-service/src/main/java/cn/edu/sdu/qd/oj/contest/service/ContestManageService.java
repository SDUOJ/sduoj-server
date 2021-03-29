/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.service;

import cn.edu.sdu.qd.oj.auth.enums.PermissionEnum;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.contest.client.SubmissionClient;
import cn.edu.sdu.qd.oj.contest.client.UserClient;
import cn.edu.sdu.qd.oj.contest.converter.ContestCreateReqConverter;
import cn.edu.sdu.qd.oj.contest.converter.ContestManageConverter;
import cn.edu.sdu.qd.oj.contest.dao.ContestDao;
import cn.edu.sdu.qd.oj.contest.dto.ContestCreateReqDTO;
import cn.edu.sdu.qd.oj.contest.dto.ContestManageDTO;
import cn.edu.sdu.qd.oj.contest.dto.ContestSubmissionExportReqDTO;
import cn.edu.sdu.qd.oj.contest.entity.ContestDO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionExportReqDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionExportResultDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ContestManageService {

    @Autowired
    private ContestDao contestDao;

    @Autowired
    private ContestCreateReqConverter contestCreateReqConverter;

    @Autowired
    private ContestManageConverter contestManageConverter;

    @Autowired
    private SubmissionClient submissionClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ContestCommonService contestCommonService;

    public Long create(ContestCreateReqDTO reqDTO) {
        ContestDO contestDO = contestCreateReqConverter.from(reqDTO);
        AssertUtils.isTrue(contestDao.save(contestDO), ApiExceptionEnum.UNKNOWN_ERROR);
        return contestDO.getContestId();
    }


    public ContestManageDTO query(long contestId) {
        ContestDO contestDO = contestDao.getById(contestId);
        AssertUtils.notNull(contestDO, ApiExceptionEnum.CONTEST_NOT_FOUND);
        return contestManageConverter.to(contestDO);
    }

    public void update(ContestManageDTO reqDTO, UserSessionDTO userSessionDTO) {
        ContestDO contestDO = contestDao.lambdaQuery().select(
                ContestDO::getContestId,
                ContestDO::getUserId,
                ContestDO::getVersion
        ).eq(ContestDO::getContestId, reqDTO.getContestId()).one();
        AssertUtils.notNull(contestDO, ApiExceptionEnum.CONTEST_NOT_FOUND);
        // 超级管理员、创建者、权限组用户 才能更新比赛详情
        AssertUtils.isTrue(contestCommonService.isContestManager(contestDO, userSessionDTO),
                ApiExceptionEnum.USER_NOT_MATCHING, "只有超级管理员、创建者才能修改比赛");

        ContestDO contestUpdateDO = contestManageConverter.from(reqDTO);
        contestUpdateDO.setParticipantNum(contestUpdateDO.getParticipants().length / 8);
        contestUpdateDO.setVersion(contestDO.getVersion());

        if (!contestDao.updateById(contestUpdateDO)) {
            throw new ApiException(ApiExceptionEnum.SERVER_BUSY);
        }
    }

    public void exportSubmission(ContestSubmissionExportReqDTO reqDTO,
                                 UserSessionDTO userSessionDTO,
                                 ZipOutputStream zipOut) throws IOException {
        Long contestId = reqDTO.getContestId();
        ContestDO contestDO = contestDao.lambdaQuery().select(
                ContestDO::getUserId,
                ContestDO::getProblems
        ).eq(ContestDO::getContestId, contestId).one();
        if (!contestCommonService.isContestManager(contestDO, userSessionDTO)) {
            throw new ApiException(ApiExceptionEnum.USER_NOT_MATCHING);
        }

        SubmissionExportReqDTO exportReqDTO = SubmissionExportReqDTO.builder()
                .contestId(reqDTO.getContestId())
                .problemId(contestDO.getProblemIdByIndex(reqDTO.getProblemIndex()))
                .userId(StringUtils.isBlank(reqDTO.getUsername()) ? null : userClient.usernameToUserId(reqDTO.getUsername()))
                .judgeTemplateId(reqDTO.getJudgeTemplateId())
                .judgeResult(reqDTO.getJudgeResult())
                .build();
        submissionClient.exportSubmission(exportReqDTO).forEach(resultDTO -> {
            try {
                ZipEntry zipEntry = new ZipEntry(submissionResultDTOToFilename(contestId, resultDTO));
                zipEntry.setSize(resultDTO.getCode().length());
                zipOut.putNextEntry(zipEntry);
                StreamUtils.copy(resultDTO.getCode(), StandardCharsets.UTF_8, zipOut);
                zipOut.closeEntry();
            } catch (Exception e) {
                throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
            }
        });
        zipOut.finish();
        zipOut.close();
    }

    private String submissionResultDTOToFilename(long contestId, SubmissionExportResultDTO resultDTO) {
        return "" + contestId + '-' + resultDTO.getUserId() + '-' + resultDTO.getProblemId() + '-' +
                resultDTO.getJudgeTemplateId() + '-' + Long.toHexString(resultDTO.getSubmissionId());
    }
}