package cn.edu.sdu.qd.oj.contest.service;

import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.common.util.ProblemCacheUtils;
import cn.edu.sdu.qd.oj.contest.client.ProblemClient;
import cn.edu.sdu.qd.oj.contest.client.SubmissionClient;
import cn.edu.sdu.qd.oj.contest.client.UserClient;
import cn.edu.sdu.qd.oj.contest.converter.ContestConvertUtils;
import cn.edu.sdu.qd.oj.contest.converter.ContestConverter;
import cn.edu.sdu.qd.oj.contest.converter.ContestListConverter;
import cn.edu.sdu.qd.oj.contest.dao.ContestDao;
import cn.edu.sdu.qd.oj.contest.dao.ContestListDao;
import cn.edu.sdu.qd.oj.contest.dto.*;
import cn.edu.sdu.qd.oj.contest.entity.ContestDO;
import cn.edu.sdu.qd.oj.contest.entity.ContestListDO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDescriptionDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionCreateReqDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListReqDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContestService {

    @Autowired
    private ContestDao contestDao;

    @Autowired
    private ContestListDao contestListDao;

    @Autowired
    private ContestConverter contestConverter;

    @Autowired
    private ContestListConverter contestListConverter;

    @Autowired
    private ProblemClient problemClient;

    @Autowired
    private SubmissionClient submissionClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ProblemCacheUtils problemCacheUtils;

    public ContestDTO queryAndValidate(Long contestId, long userId) {
        ContestDO contestDO = contestDao.getById(contestId);
        if (contestDO == null) {
            throw new ApiException(ApiExceptionEnum.CONTEST_NOT_FOUND);
        }

        // 鉴权  TODO: feature 能力架构设计，解耦+去魔法值
        Map<String, String> featureMap = ContestConvertUtils.stringToMap(contestDO.getFeatures());
        String openness = Optional.ofNullable(featureMap).map(map -> map.get("openness")).orElse("");
        if (!contestDO.containsUserIdInParticipants(userId) && "private".equals(openness)) {
            contestDO.setProblems(null);
            contestDO.setMarkdownDescription(null);
        }
        // 比赛未开始无法查题
        if (contestDO.getGmtStart().after(new Date())) {
            contestDO.setProblems(null);
        }

        return contestConverter.to(contestDO);
    }

    @Transactional
    public void participate(Long contestId, Long userId, String password) {
        ContestDO contestDO = contestDao.lambdaQuery().select(
                ContestDO::getContestId,
                ContestDO::getFeatures,
                ContestDO::getPassword,
                ContestDO::getVersion,
                ContestDO::getParticipants
        ).eq(ContestDO::getContestId, contestId).one();
        if (contestDO == null) {
            throw new ApiException(ApiExceptionEnum.CONTEST_NOT_FOUND);
        }

        // TODO: feature 能力架构设计，解耦+去魔法值
        Map<String, String> featureMap = ContestConvertUtils.stringToMap(contestDO.getFeatures());
        String openness = Optional.ofNullable(featureMap).map(map -> map.get("openness")).orElse("");
        switch (openness) {
            case "public":
                break;
            case "protected":
            case "private":
                if (!contestDO.getPassword().equals(password)) {
                    throw new ApiException(ApiExceptionEnum.CONTEST_PASSWORD_NOT_MATCHING);
                }
                break;
        }

        // 新增一个用户到比赛
        if (!contestDO.addOneParticipant(userId)) {
            throw new ApiException(ApiExceptionEnum.CONTEST_HAD_PARTICIPATED);
        }
        // 密码不进行更改
        contestDO.setPassword(null);

        if (!contestDao.updateById(contestDO)) { // 此时乐观锁会自动填入
            throw new ApiException(ApiExceptionEnum.SERVER_BUSY);
        }

        userClient.addUserParticipateContest(userId, contestId);
    }

    public PageResult<ContestListDTO> page(ContestListReqDTO reqDTO) {
        Page<ContestListDO> pageResult = contestListDao.lambdaQuery()
                .orderByDesc(ContestListDO::getGmtStart)
                .page(new Page<>(reqDTO.getPageNow(), reqDTO.getPageSize()));
        return new PageResult<>(pageResult.getPages(), contestListConverter.to(pageResult.getRecords()));
    }

    public ContestListDTO queryUpcomingContest() {
        ContestListDO contestListDO = contestListDao.lambdaQuery()
                .orderByAsc(ContestListDO::getGmtStart)
                .ge(ContestListDO::getGmtStart, new Date()).one();
        return contestListConverter.to(contestListDO);
    }

    public ContestProblemDTO queryProblem(long contestId, int problemIndex, long userId) {
        ContestDO contestDO = queryContestAndValidate(contestId, userId);
        ContestProblemListDTO contestProblemListDTO = contestDO.getProblemCodeByIndex(problemIndex);
        // RPC查询题目
        ProblemDTO problemDTO = problemClient.queryAndValidate(contestProblemListDTO.getProblemCode(), contestProblemListDTO.getProblemDescriptionId(), userId);
        // 类型转换
        ProblemDescriptionDTO problemDescriptionDTO = problemDTO.getProblemDescriptionDTO();
        ContestProblemDescriptionDTO contestProblemDescriptionDTO = ContestProblemDescriptionDTO.builder()
                .htmlDescription(problemDescriptionDTO.getHtmlDescription())
                .htmlHint(problemDescriptionDTO.getHtmlHint())
                .htmlInput(problemDescriptionDTO.getHtmlInput())
                .htmlOutput(problemDescriptionDTO.getHtmlOutput())
                .htmlSampleInput(problemDescriptionDTO.getHtmlSampleInput())
                .htmlSampleOutout(problemDescriptionDTO.getHtmlSampleOutout())
                .markdownDescription(problemDescriptionDTO.getMarkdownDescription())
                .build();
        ContestProblemDTO contestProblemDTO = ContestProblemDTO.builder()
                .languages(problemDTO.getLanguages())
                .timeLimit(problemDTO.getTimeLimit())
                .memoryLimit(problemDTO.getMemoryLimit())
                .problemCode(problemDTO.getProblemCode())
                .problemTitle(contestProblemListDTO.getProblemTitle())
                .problemWeight(contestProblemListDTO.getProblemWeight())
                .problemDescriptionDTO(contestProblemDescriptionDTO)
                .build();
        return contestProblemDTO;
    }

    private ContestDO queryContestAndValidate(long contestId, long userId) {
        ContestDO contestDO = contestDao.lambdaQuery().select(
                ContestDO::getFeatures,
                ContestDO::getGmtStart,
                ContestDO::getGmtEnd,
                ContestDO::getProblems,
                ContestDO::getParticipants
        ).eq(ContestDO::getContestId, contestId).one();
        if (contestDO == null) {
            throw new ApiException(ApiExceptionEnum.CONTEST_NOT_FOUND);
        }

        // TODO: feature 能力架构设计，解耦+去魔法值
        Map<String, String> featureMap = ContestConvertUtils.stringToMap(contestDO.getFeatures());
        String openness = Optional.ofNullable(featureMap).map(map -> map.get("openness")).orElse("");
        if (!contestDO.containsUserIdInParticipants(userId) && "private".equals(openness)) {
            throw new ApiException(ApiExceptionEnum.CONTEST_NOT_PARTICIPATE);
        }

        if (contestDO.getGmtStart().after(new Date())) {
            throw new ApiException(ApiExceptionEnum.CONTEST_NOT_BEGIN);
        }
        return contestDO;
    }


    public String createSubmission(ContestSubmissionCreateReqDTO reqDTO) {
        ContestDO contestDO = queryContestAndValidate(reqDTO.getContestId(), reqDTO.getUserId());

        // 未登记参加比赛不能做提交    TODO: 更好的设计方式
        if (!contestDO.containsUserIdInParticipants(reqDTO.getUserId())) {
            throw new ApiException(ApiExceptionEnum.CONTEST_NOT_PARTICIPATE);
        }
        // 比赛未开始不能提交
        if (contestDO.getGmtStart().after(new Date())) {
            throw new ApiException(ApiExceptionEnum.CONTEST_NOT_BEGIN);
        }

        ContestProblemListDTO contestProblemListDTO = contestDO.getProblemCodeByIndex(reqDTO.getProblemIndex());
        String problemCode = contestProblemListDTO.getProblemCode();
        SubmissionCreateReqDTO submissionCreateReqDTO = SubmissionCreateReqDTO.builder()
                .problemCode(problemCode)
                .language(reqDTO.getLanguage())
                .code(reqDTO.getCode())
                .userId(reqDTO.getUserId())
                .ipv4(reqDTO.getIpv4())
                .build();
        long submissionId = submissionClient.create(reqDTO.getContestId(), submissionCreateReqDTO);
        return Long.toHexString(submissionId);
    }

    public PageResult<ContestSubmissionListDTO> listSubmission(ContestSubmissionListReqDTO reqDTO, Long requestUserId) {
        ContestDO contestDO = queryContestAndValidate(reqDTO.getContestId(), requestUserId);
        // TODO: 判定比赛是否不支持查询他人提交 / 判定比赛是否赛时不支持查询他人提交



        // 构造 problemCode To ProblemIndex Map
        List<ContestProblemListDTO> contestProblemListDTOList = ContestConvertUtils.problemsTo(contestDO.getProblems());
        List<String> problemCodeList = contestProblemListDTOList.stream().map(ContestProblemListDTO::getProblemCode).collect(Collectors.toList());
        Map<String, Integer> problemCodeToProblemIndexMap = new HashMap<>(contestProblemListDTOList.size());
        for (int i = 0, n = contestProblemListDTOList.size(); i < n; i++) {
            problemCodeToProblemIndexMap.put(contestProblemListDTOList.get(i).getProblemCode(), i + 1);
        }

        ContestProblemListDTO contestProblemListDTO = contestDO.getProblemCodeByIndex(reqDTO.getProblemIndex());
        String problemCode = Optional.ofNullable(contestProblemListDTO).map(ContestProblemListDTO::getProblemCode).orElse(null);

        SubmissionListReqDTO submissionListReqDTO = SubmissionListReqDTO.builder()
                .pageNow(reqDTO.getPageNow())
                .pageSize(reqDTO.getPageSize())
                .orderBy(reqDTO.getOrderBy())
                .ascending(reqDTO.getAscending())
                .judgeResult(reqDTO.getJudgeResult())
                .language(reqDTO.getLanguage())
                .problemCode(problemCode)
                .username(reqDTO.getUsername())
                .problemCodeList(problemCodeList)
                .build();
        try {
            PageResult<SubmissionListDTO> pageResult = submissionClient.list(reqDTO.getContestId(), submissionListReqDTO);
            List<ContestSubmissionListDTO> contestSubmissionListDTOList = pageResult.getRows().stream().map(submissionListDTO -> {
                Integer problemIndex = problemCodeToProblemIndexMap.get(submissionListDTO.getProblemCode());
                ContestSubmissionListDTO contestSubmissionListDTO = new ContestSubmissionListDTO();
                BeanUtils.copyProperties(submissionListDTO, contestSubmissionListDTO);
                contestSubmissionListDTO.setProblemCode(String.valueOf(problemIndex));
                contestSubmissionListDTO.setProblemTitle(contestProblemListDTOList.get(problemIndex - 1).getProblemTitle());
                return contestSubmissionListDTO;
            }).collect(Collectors.toList());
            return new PageResult<>(pageResult.getTotalPage(), contestSubmissionListDTOList);
        } catch (InternalApiException exception) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
    }

    public List<String> queryACProblem(Long userId, long contestId) {
        ContestDO contestDO = queryContestAndValidate(contestId, userId);
        List<ContestProblemListDTO> contestProblemListDTOList = ContestConvertUtils.problemsTo(contestDO.getProblems());
        Map<String, String> problemCodeToProblemIndexMap = new HashMap<>(contestProblemListDTOList.size());
        for (int i = 0, n = contestProblemListDTOList.size(); i < n; i++) {
            problemCodeToProblemIndexMap.put(contestProblemListDTOList.get(i).getProblemCode(), String.valueOf(i + 1));
        }
        List<String> problemCodeList = Optional.ofNullable(userClient.queryACProblem(userId, contestId)).orElse(Lists.newArrayList());
        return problemCodeList.stream().map(problemCodeToProblemIndexMap::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public SubmissionDTO querySubmission(Long submissionId, long contestId, Long userId) throws InternalApiException {
        SubmissionDTO submissionDTO = submissionClient.query(submissionId, contestId);
        if (submissionDTO == null) {
            return null;
        }

        ContestDO contestDO = queryContestAndValidate(contestId, userId);

        List<ContestProblemListDTO> contestProblemListDTOList = ContestConvertUtils.problemsTo(contestDO.getProblems());
        Map<Long, Integer> problemIdToProblemIndexMap = new HashMap<>(contestProblemListDTOList.size());
        for (int i = 0, n = contestProblemListDTOList.size(); i < n; i++) {
            problemIdToProblemIndexMap.put(problemCacheUtils.getProblemId(contestProblemListDTOList.get(i).getProblemCode()), i + 1);
        }

        // problemId、problemCode 脱敏
        submissionDTO.setProblemCode(problemIdToProblemIndexMap.get(submissionDTO.getProblemId()).toString());
        submissionDTO.setProblemId(null);

        // code 脱敏      TODO: 管理员可以看所有代码
        if (!submissionDTO.getUserId().equals(userId)) {
            submissionDTO.setCode(null);
            submissionDTO.setCheckpointResults(null);
        }
        return submissionDTO;
    }
}
