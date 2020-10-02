/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.service;

import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.common.util.ProblemCacheUtils;
import cn.edu.sdu.qd.oj.common.util.SnowflakeIdWorker;
import cn.edu.sdu.qd.oj.common.util.UserCacheUtils;
import cn.edu.sdu.qd.oj.submit.client.UserClient;
import cn.edu.sdu.qd.oj.submit.converter.SubmissionConverter;
import cn.edu.sdu.qd.oj.submit.converter.SubmissionListConverter;
import cn.edu.sdu.qd.oj.submit.dao.SubmissionDao;
import cn.edu.sdu.qd.oj.submit.dto.*;
import cn.edu.sdu.qd.oj.submit.entity.SubmissionDO;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @ClassName SubmitService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/6 16:04
 * @Version V1.0
 **/

@Service
@Slf4j
public class SubmitService {

    @Autowired
    private SubmissionDao submissionDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserClient userClient;

    @Autowired
    private UserCacheUtils userCacheUtils;

    @Autowired
    private ProblemCacheUtils problemCacheUtils;

    @Autowired
    private SubmissionConverter submissionConverter;

    @Autowired
    private SubmissionListConverter submissionListConverter;

    // TODO: 临时采用 IP+PID 格式, 生产时加配置文件 Autowired
    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    @Transactional
    public Long createSubmission(SubmissionCreateReqDTO submissionUpdateReqDTO) {
        // TODO: 校验提交语言支持、校验题目对用户权限


        long problemId = problemCacheUtils.getProblemId(submissionUpdateReqDTO.getProblemCode());
        long snowflaskId = snowflakeIdWorker.nextId();
        SubmissionDO submissionDO = SubmissionDO.builder()
                .submissionId(snowflaskId)
                .code(submissionUpdateReqDTO.getCode())
                .ipv4(submissionUpdateReqDTO.getIpv4())
                .codeLength(submissionUpdateReqDTO.getCode().length())
                .language(submissionUpdateReqDTO.getLanguage())
                .problemId(problemId)
                .userId(submissionUpdateReqDTO.getUserId())
                .build();
        if (!submissionDao.save(submissionDO)) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        try {
            // TODO: 魔法值解决
            Map<String, Object> msg = new HashMap<>();
            msg.put("event", "submissionCreated");
            msg.put("submissionId", Long.toHexString(submissionDO.getSubmissionId()));
            this.rabbitTemplate.convertAndSend("", "judge_queue", msg);
        } catch (Exception e) {
            log.error("[submit] submissionCreate MQ send error", e);
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        return submissionDO.getSubmissionId();
    }


    public SubmissionDTO queryById(long submissionId) {
        SubmissionDO submissionDO = submissionDao.getById(submissionId);
        if (submissionDO == null) {
            throw new ApiException(ApiExceptionEnum.SUBMISSION_NOT_FOUND);
        }
        SubmissionDTO submissionDTO = submissionConverter.to(submissionDO);
        submissionDTO.setCheckpointNum(problemCacheUtils.getProblemCheckpointNum(submissionDTO.getProblemId()));
        submissionDTO.setUsername(userCacheUtils.getUsername(submissionDTO.getUserId()));
        return submissionDTO;
    }

    public PageResult<SubmissionListDTO> querySubmissionByPage(SubmissionListReqDTO reqDTO) throws InternalApiException {
        // 填充字段
        if (StringUtils.isNotBlank(reqDTO.getUsername())) {
            reqDTO.setUserId(userClient.queryUserId(reqDTO.getUsername()));
        }
        if (StringUtils.isNotBlank(reqDTO.getProblemCode())) {
            reqDTO.setProblemId(problemCacheUtils.getProblemId(reqDTO.getProblemCode()));
        }

        // 构建查询列
        LambdaQueryChainWrapper<SubmissionDO> query = submissionDao.lambdaQuery().select(
            SubmissionDO::getSubmissionId,
            SubmissionDO::getProblemId,
            SubmissionDO::getUserId,
            SubmissionDO::getLanguage,
            SubmissionDO::getGmtCreate,
            SubmissionDO::getGmtModified,
            SubmissionDO::getIsPublic,
            SubmissionDO::getValid,
            SubmissionDO::getJudgeResult,
            SubmissionDO::getJudgeScore,
            SubmissionDO::getUsedTime,
            SubmissionDO::getUsedMemory,
            SubmissionDO::getCodeLength
        );

        // 排序字段
        Optional.ofNullable(reqDTO.getOrderBy()).ifPresent(orderBy -> {
            switch (reqDTO.getOrderBy()) {
                case "usedTime":
                    query.orderBy(true, reqDTO.getAscending(), SubmissionDO::getUsedTime);
                    break;
                case "usedMemory":
                    query.orderBy(true, reqDTO.getAscending(), SubmissionDO::getUsedMemory);
                    break;
                case "gmtCreate":
                    query.orderBy(true, reqDTO.getAscending(), SubmissionDO::getGmtCreate);
                    break;
            }
            if ("gmtCreate".equals(orderBy)) {
                query.orderBy(true, reqDTO.getAscending(), SubmissionDO::getGmtCreate);
            } else {
                // 默认按时间排序
                query.orderByDesc(SubmissionDO::getGmtCreate);
            }
        });

        // 等值字段
        Optional.of(reqDTO).map(SubmissionListReqDTO::getLanguage).filter(StringUtils::isNotBlank).ifPresent(language -> {
            query.eq(SubmissionDO::getLanguage, language);
        });
        Optional.of(reqDTO).map(SubmissionListReqDTO::getJudgeResult).ifPresent(judgeResult -> {
            query.eq(SubmissionDO::getJudgeResult, judgeResult);
        });
        Optional.of(reqDTO).map(SubmissionListReqDTO::getUserId).ifPresent(userId -> {
            query.eq(SubmissionDO::getUserId, userId);
        });
        Optional.of(reqDTO).map(SubmissionListReqDTO::getProblemId).ifPresent(problemId -> {
            query.eq(SubmissionDO::getProblemId, problemId);
        });

        // 查询数据
        Page<SubmissionDO> pageResult = query.page(new Page<>(reqDTO.getPageNow(), reqDTO.getPageSize()));
        List<SubmissionListDTO> submissionListDTOList = submissionListConverter.to(pageResult.getRecords());

        // 置 problemCode
        if (StringUtils.isNotBlank(reqDTO.getProblemCode())) {
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setProblemCode(reqDTO.getProblemCode()));
        } else {
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setProblemCode(problemCacheUtils.getProblemCode(submissionListDTO.getProblemId())));
        }
        // 置 username
        if (StringUtils.isNotBlank(reqDTO.getUsername())) {
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setUsername(reqDTO.getUsername()));
        } else {
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setUsername(userCacheUtils.getUsername(submissionListDTO.getUserId())));
        }
        // 置题目标题
        if (reqDTO.getProblemId() != null) {
            String problemTitle = problemCacheUtils.getProblemTitle(reqDTO.getProblemId());
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setProblemTitle(problemTitle));
        } else {
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setProblemTitle(problemCacheUtils.getProblemTitle(submissionListDTO.getProblemId())));
        }

        return new PageResult<>(pageResult.getPages(), submissionListDTOList);
    }

}