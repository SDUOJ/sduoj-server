/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.filesys.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName CheckpointFileSystemProperties
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/6 16:34
 * @Version V1.0
 **/

@Getter
@Setter
@ConfigurationProperties(prefix = "sduoj.filesys")
public class FileSystemProperties {
    private String baseDir; // 工作目录
}