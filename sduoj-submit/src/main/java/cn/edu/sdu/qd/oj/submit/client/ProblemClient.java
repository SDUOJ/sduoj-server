/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.client;

import cn.edu.sdu.qd.oj.problem.api.ProblemApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @InterfaceName ProblemClient
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/9 17:11
 * @Version V1.0
 **/

@FeignClient(value = "problem-service")
public interface ProblemClient extends ProblemApi {
}