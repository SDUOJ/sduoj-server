/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj;

import org.apache.ibatis.annotations.Mapper;

/**
 * @InterfaceName NoWarnMapper
 * @Description 无它用, 仅避免 Application 启动器在 doScan 扫同级目录
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@Mapper
public interface NoWarnMapper {
}