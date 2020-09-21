/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.ResponseResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.user.service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ApiResponseBody
    public Void register(@Valid @RequestBody UserDTO userDTO) {
        this.userService.register(userDTO);
        return null;
    }

    @PostMapping("login")
    @ApiResponseBody
    public ResponseEntity<ResponseResult<UserSessionDTO>> login(RequestEntity<String> entity) throws InternalApiException {
        String username = null, password = null;
        try {
            JSONObject json = JSON.parseObject(entity.getBody());
            username = json.getString("username");
            password = json.getString("password");
        } catch (Exception ignore) {
        }

        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            // 登录校验
            UserDTO userDTO = this.userService.verify(username, password);
            if (userDTO == null) {
                throw new ApiException(ApiExceptionEnum.PASSWORD_NOT_MATCHING);
            }
            UserSessionDTO userSessionDTO = UserSessionDTO.builder()
                    .userId(userDTO.getUserId())
                    .username(userDTO.getUsername())
                    .build();
            // Set-Header
            HttpHeaders headers = new HttpHeaders();
            // TODO: 魔法值解决
            headers.set("SDUOJUserInfo", JSON.toJSONString(userSessionDTO));
            return new ResponseEntity<>(ResponseResult.ok(userSessionDTO), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(ResponseResult.error(null), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("logout")
    @ApiResponseBody
    public ResponseEntity<ResponseResult<Void>> logout() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("SDUOJUserInfo", "Logout");
        return new ResponseEntity<>(ResponseResult.ok(), headers, HttpStatus.OK);
    }
}
