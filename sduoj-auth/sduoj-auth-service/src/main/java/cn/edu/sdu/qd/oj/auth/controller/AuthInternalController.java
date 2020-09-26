package cn.edu.sdu.qd.oj.auth.controller;

import cn.edu.sdu.qd.oj.auth.api.PermissionApi;
import cn.edu.sdu.qd.oj.auth.dto.PermissionDTO;
import cn.edu.sdu.qd.oj.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthInternalController implements PermissionApi {

    @Autowired
    private AuthService authService;

    @Override
    public void sync(List<PermissionDTO> permissionDTOList) {
        if (!permissionDTOList.isEmpty()) {
            authService.syncNewPermissionUrl(permissionDTOList);
        }
    }

    @Override
    public List<PermissionDTO> listAll() {
        return authService.listAll();
    }
}
