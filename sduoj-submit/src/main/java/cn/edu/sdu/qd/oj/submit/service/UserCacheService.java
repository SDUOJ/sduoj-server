/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.service;

import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.submit.client.UserClient;
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
public class UserCacheService {
    // TODO: 结合 MQ 保证数据一致性
    private Map<Integer, String> userIdToUsernameMap;

    @Autowired
    private UserClient userClient;

    @PostConstruct
    public void init() {
        try {
            userIdToUsernameMap = userClient.queryIdToNameMap();
        } catch (InternalApiException e) {
            log.error("[Problem]: UserCacheService Init Failed!");
            userIdToUsernameMap = null;
        }
    }

    public String getUsername(Integer userId) {
        if (userId == null) {
            return null;
        }
        if (userIdToUsernameMap == null) {
            init();
            if (userIdToUsernameMap == null) {
                return String.valueOf(userId);
            }
        }
        return userIdToUsernameMap.get(userId);
    }
}