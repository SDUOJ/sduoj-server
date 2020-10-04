package cn.edu.sdu.qd.oj.contest.client;

import cn.edu.sdu.qd.oj.problem.api.ProblemApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(ProblemApi.SERVICE_NAME)
public interface ProblemClient extends ProblemApi {
}
