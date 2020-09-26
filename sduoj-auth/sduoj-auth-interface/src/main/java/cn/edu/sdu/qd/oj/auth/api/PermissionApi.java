package cn.edu.sdu.qd.oj.auth.api;

import cn.edu.sdu.qd.oj.auth.dto.PermissionDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/internal/auth")
public interface PermissionApi {
    String SERVICE_NAME = "auth-service";

    /**
    * @Description 同步微服务 URL 到权限中心
    * @param permissionDTOList
    * @return void
    **/
    @PostMapping(value = "/sync", consumes = "application/json")
    void sync(@RequestBody List<PermissionDTO> permissionDTOList);

    /**
    * @Description 查询所有url权限信息
    **/
    @GetMapping(value = "/listAll")
    List<PermissionDTO> listAll();
}
