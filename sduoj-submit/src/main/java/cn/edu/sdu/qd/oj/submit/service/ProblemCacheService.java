/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.service;

import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.submit.client.ProblemClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @ClassName UserCacheService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/8 11:33
 * @Version V1.0
 **/

@Service
@Slf4j
public class ProblemCacheService {
    // TODO: 结合 MQ 保证数据一致性
    private Map<Integer, String> problemIdToProblemTitleMap;

    @Autowired
    private ProblemClient problemClient;

    @PostConstruct
    public void init() {
        try {
            problemIdToProblemTitleMap = problemClient.queryIdToTitleMap();
        } catch (InternalApiException e) {
            log.error("[Problem]: ProblemCacheService Init Failed!");
            problemIdToProblemTitleMap = null;
        }
    }

    public String getProblemTitle(Integer problemId) {
        if (problemId == null) {
            return null;
        }
        if (problemIdToProblemTitleMap == null) {
            init();
            if (problemIdToProblemTitleMap == null) {
                return String.valueOf(problemId);
            }
        }
        return problemIdToProblemTitleMap.get(problemId);
    }
}