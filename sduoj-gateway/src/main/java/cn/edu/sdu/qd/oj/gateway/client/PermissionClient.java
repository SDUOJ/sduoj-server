package cn.edu.sdu.qd.oj.gateway.client;

import cn.edu.sdu.qd.oj.auth.api.PermissionApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(PermissionApi.SERVICE_NAME)
public interface PermissionClient extends PermissionApi {
}
