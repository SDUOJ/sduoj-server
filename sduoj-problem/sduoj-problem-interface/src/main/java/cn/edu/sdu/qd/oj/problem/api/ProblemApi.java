/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.api;

import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @InterfaceName ProblemApi
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/9 17:05
 * @Version V1.0
 **/

public interface ProblemApi {

    @GetMapping("/internal/problem/queryid2title")
    Map<Integer, String> queryIdToTitleMap() throws InternalApiException;
}