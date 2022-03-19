/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.filesys.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.InputStream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BinaryFileUploadReqDTO extends BaseDTO {
    private String filename;
    private byte[] bytes;
    private Long size;

    @JsonIgnore
    private InputStream inputStream;
}
