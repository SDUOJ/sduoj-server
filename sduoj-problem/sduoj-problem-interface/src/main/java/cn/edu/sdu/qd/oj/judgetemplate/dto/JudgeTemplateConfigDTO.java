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
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;


/**
* @Description cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateDTO#shellScript 的实体类
**/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JudgeTemplateConfigDTO extends BaseDTO {

    @Valid
    @NotNull(message = "user must be not null")
    private TemplateConfig user;

    @Valid
    @Nullable
    private TemplateConfig spj;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class TemplateConfig extends BaseDTO {

        @Valid
        @NotNull(message = "compile must be not null")
        private Compile compile;

        @Valid
        @NotNull(message = "run must be not null")
        private Run run;

        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @EqualsAndHashCode(callSuper = true)
        public static class Compile extends BaseDTO {

            @NotNull
            @NotBlank
            private String srcName;

            @NotNull
            private Integer maxCpuTime;

            private Integer maxRealTime;

            @NotNull
            private Integer maxMemory;

            @NotNull
            private String[] commands;

            public String getSrcName() {
                return srcName;
            }

            public void setSrcName(String srcName) {
                this.srcName = srcName;
            }

            public Integer getMaxCpuTime() {
                return maxCpuTime;
            }

            public void setMaxCpuTime(Integer maxCpuTime) {
                this.maxCpuTime = maxCpuTime;
            }

            public Integer getMaxRealTime() {
                return Optional.ofNullable(maxRealTime).orElse(maxCpuTime * 2);
            }

            public void setMaxRealTime(Integer maxRealTime) {
                this.maxRealTime = maxRealTime;
            }

            public Integer getMaxMemory() {
                return maxMemory;
            }

            public void setMaxMemory(Integer maxMemory) {
                this.maxMemory = maxMemory;
            }

            public String[] getCommands() {
                return commands;
            }

            public void setCommands(String[] commands) {
                this.commands = commands;
            }
        }


        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @EqualsAndHashCode(callSuper = true)
        public static class Run extends BaseDTO {
            @NotNull
            @NotBlank(message = "run command must not be null")
            private String command;
            private String seccompRule;
            private Integer maxCpuTimeFactor;
            private Integer maxRealTimeFactor;
            private Integer maxMemoryFactor;
            private String[] envs;

            public String getCommand() {
                return command;
            }

            public void setCommand(String command) {
                this.command = command;
            }

            public String getSeccompRule() {
                return seccompRule;
            }

            public void setSeccompRule(String seccompRule) {
                this.seccompRule = seccompRule;
            }

            public Integer getMaxCpuTimeFactor() {
                return Optional.ofNullable(maxCpuTimeFactor).orElse(1);
            }

            public void setMaxCpuTimeFactor(Integer maxCpuTimeFactor) {
                this.maxCpuTimeFactor = maxCpuTimeFactor;
            }

            public Integer getMaxRealTimeFactor() {
                return Optional.ofNullable(maxRealTimeFactor).orElse(1);
            }

            public void setMaxRealTimeFactor(Integer maxRealTimeFactor) {
                this.maxRealTimeFactor = maxRealTimeFactor;
            }

            public Integer getMaxMemoryFactor() {
                return Optional.ofNullable(maxMemoryFactor).orElse(1);
            }

            public void setMaxMemoryFactor(Integer maxMemoryFactor) {
                this.maxMemoryFactor = maxMemoryFactor;
            }

            public String[] getEnvs() {
                return envs;
            }

            public void setEnvs(String[] envs) {
                this.envs = envs;
            }
        }
    }
}
