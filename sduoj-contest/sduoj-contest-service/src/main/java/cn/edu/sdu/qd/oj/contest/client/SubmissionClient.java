package cn.edu.sdu.qd.oj.contest.client;

import cn.edu.sdu.qd.oj.submit.api.SubmissionApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(SubmissionApi.SERVICE_NAME)
public interface SubmissionClient extends SubmissionApi {
}
