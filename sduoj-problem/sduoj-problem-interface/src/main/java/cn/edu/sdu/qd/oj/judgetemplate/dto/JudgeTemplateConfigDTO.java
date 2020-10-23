/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.judgetemplate.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;



/**
* @Description cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateDTO#shellScript 的实体类
**/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JudgeTemplateConfigDTO extends BaseDTO {

    private TemplateConfig user;
    private TemplateConfig spj;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class TemplateConfig extends BaseDTO {
        private Compile compile;
        private Run run;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @EqualsAndHashCode(callSuper = true)
        public static class Compile extends BaseDTO {
            private String srcName;
            private String exeName;
            private Integer maxCpuTime;
            private Integer maxRealTime;
            private Integer maxMemory;
            private String[] commands;
        }


        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @EqualsAndHashCode(callSuper = true)
        public static class Run extends BaseDTO {
            private String[] commands;
            private String seccompRule;
            private Integer maxCpuTimeFactor;
            private Integer maxRealTimeFactor;
            private Integer maxMemoryFactor;
            private String[] envs;
            private String[] args;
        }
    }
}
