package cn.edu.sdu.qd.oj.problem.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.problem.dto.ProblemTagDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemTagDO;

@org.mapstruct.Mapper(componentModel = "spring")
public interface ProblemTagConverter extends BaseConverter<ProblemTagDO, ProblemTagDTO> {
}
