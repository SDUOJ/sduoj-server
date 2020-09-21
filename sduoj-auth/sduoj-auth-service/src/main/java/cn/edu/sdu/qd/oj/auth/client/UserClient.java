package cn.edu.sdu.qd.oj.auth.client;

import cn.edu.sdu.qd.oj.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(UserApi.SERVICE_NAME)
public interface UserClient extends UserApi {
}
