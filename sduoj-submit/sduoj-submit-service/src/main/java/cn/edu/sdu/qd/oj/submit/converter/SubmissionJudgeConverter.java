/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionJudgeDTO;
import cn.edu.sdu.qd.oj.submit.entity.SubmissionDO;

@org.mapstruct.Mapper(componentModel = "spring")
public interface SubmissionJudgeConverter extends BaseConverter<SubmissionDO, SubmissionJudgeDTO> {
}