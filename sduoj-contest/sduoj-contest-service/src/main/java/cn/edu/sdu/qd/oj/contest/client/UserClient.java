/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.client;

import cn.edu.sdu.qd.oj.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(UserApi.SERVICE_NAME)
public interface UserClient extends UserApi {
}