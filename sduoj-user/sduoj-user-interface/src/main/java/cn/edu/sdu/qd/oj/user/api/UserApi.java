/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.api;

import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @InterfaceName UserApi
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/27 14:56
 * @Version V1.0
 **/

public interface UserApi {
    @PostMapping("/internal/user/verify")
    UserDTO verify(@RequestParam("username") String username,
                   @RequestParam("password") String password) throws InternalApiException;

    @PostMapping("/internal/user/query")
    UserDTO query(@RequestParam("userId") Integer userId) throws InternalApiException;

    @PostMapping("/internal/user/queryuserid")
    Integer queryUserId(@RequestParam("username") String username) throws InternalApiException;

    @GetMapping("/internal/user/queryid2name")
    Map<Integer, String> queryIdToNameMap() throws InternalApiException;
}