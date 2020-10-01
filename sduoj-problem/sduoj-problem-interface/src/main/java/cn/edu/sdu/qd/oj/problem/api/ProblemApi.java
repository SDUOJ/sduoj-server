/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.api;

import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @InterfaceName ProblemApi
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/9 17:05
 * @Version V1.0
 **/

@RequestMapping("/internal/problem")
public interface ProblemApi {
    String SERVICE_NAME = "problem-service";

    @GetMapping("/queryIdToTitleMap")
    Map<Long, String> queryIdToTitleMap() throws InternalApiException;
}