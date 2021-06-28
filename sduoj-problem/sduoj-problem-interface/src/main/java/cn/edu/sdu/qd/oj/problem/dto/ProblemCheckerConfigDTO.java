package cn.edu.sdu.qd.oj.problem.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateConfigDTO;
import cn.edu.sdu.qd.oj.judgetemplate.util.TemplateConfigDeserializer;
import cn.edu.sdu.qd.oj.judgetemplate.util.TemplateConfigSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 将 JudgeTemplateConfigDTO 中的 spj 业务进行重构，改成题目的 checker
 * 预定义 10 个 checker (lcmp.cpp, rcmp4.cpp 等)
 * checker 支持自定义，即原来 SPJ 的能力 （原来由支撑文件提供的能力改为单文件，不再是一个 zip 包）
 * @author zhangt2333
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemCheckerConfigDTO extends BaseDTO {

    /**
     * 预定义checker的名字或自定义checker的代码
     */
    @NotNull(message = "source 不能为空")
    @NotBlank(message = "source 不能为空串")
    private String source;

    /**
     * SPJ 为 null 时，source 应该为预定义 checker 的文件名
     */
    @Nullable
    @JsonSerialize(using = TemplateConfigSerializer.class)
    @JsonDeserialize(using = TemplateConfigDeserializer.class)
    private JudgeTemplateConfigDTO.TemplateConfig spj;
}
