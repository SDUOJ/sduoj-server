package cn.edu.sdu.qd.oj.problem.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.problem.dto.ProblemListDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;

@org.mapstruct.Mapper(componentModel = "spring")
public interface ProblemListConverter extends BaseConverter<ProblemDO, ProblemListDTO> {
}
