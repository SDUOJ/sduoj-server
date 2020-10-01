package cn.edu.sdu.qd.oj.problem.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDescriptionListDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDescriptionDO;

@org.mapstruct.Mapper(componentModel = "spring")
public interface ProblemDescriptionListConverter extends BaseConverter<ProblemDescriptionDO, ProblemDescriptionListDTO> {
}
