/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.Objects;

/**
 * UserFeatureDTO
 * 注意 featureDTO 内的字段需要给默认值
 * @author zhangt2333
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserFeatureDTO extends BaseDTO {

    private Integer banThirdParty = 0;

    private Integer banEmailUpdate = 0;

    public boolean isBanThirdParty() {
        return Objects.equals(1, banThirdParty);
    }

    public boolean isBanEmailUpdate() {
        return Objects.equals(1, banEmailUpdate);
    }
}