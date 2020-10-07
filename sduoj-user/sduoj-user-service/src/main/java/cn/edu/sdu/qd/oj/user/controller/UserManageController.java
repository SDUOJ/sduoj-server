package cn.edu.sdu.qd.oj.user.controller;

import cn.edu.sdu.qd.oj.common.annotation.UserSession;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import cn.edu.sdu.qd.oj.user.dto.UserListReqDTO;
import cn.edu.sdu.qd.oj.user.dto.UserManageUpdateReqDTO;
import cn.edu.sdu.qd.oj.user.service.UserManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private UserManageService userManageService;

    @GetMapping("/list")
    @ApiResponseBody
    public PageResult<UserDTO> list(UserListReqDTO reqDTO,
                                    @UserSession UserSessionDTO userSessionDTO) {
        PageResult<UserDTO> pageResult = userManageService.list(reqDTO);
        // TODO: 根据 superadmin、admin 权限进行脱敏
        return pageResult;
    }

    @PostMapping("/update")
    @ApiResponseBody
    public Void update(@RequestBody UserManageUpdateReqDTO reqDTO,
                       @UserSession UserSessionDTO userSessionDTO) {
        // superadmin 才能改密码+改权限
        if (!userSessionDTO.getRoles().contains("superadmin")) {
            reqDTO.setPassword(null);
            reqDTO.setRoles(null);
        }
        userManageService.update(reqDTO);
        return null;
    }

    @PostMapping("/addUsers")
    @ApiResponseBody
    public Void addUsers(@RequestBody @Valid List<UserDTO> userDTOList,
                         @UserSession UserSessionDTO userSessionDTO) {
        userManageService.addUsers(userDTOList);
        return null;
    }

}
