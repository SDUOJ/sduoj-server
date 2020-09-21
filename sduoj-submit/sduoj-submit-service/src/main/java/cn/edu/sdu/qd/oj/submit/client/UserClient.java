/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.client;

import cn.edu.sdu.qd.oj.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName UserClient
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/5 11:28
 * @Version V1.0
 **/

@FeignClient(UserApi.SERVICE_NAME)
public interface UserClient extends UserApi {
}
