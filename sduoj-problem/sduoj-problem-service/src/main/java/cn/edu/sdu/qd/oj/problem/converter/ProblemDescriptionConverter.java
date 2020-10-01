package cn.edu.sdu.qd.oj.problem.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDescriptionDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDescriptionDO;

@org.mapstruct.Mapper(componentModel = "spring")
public interface ProblemDescriptionConverter extends BaseConverter<ProblemDescriptionDO, ProblemDescriptionDTO> {
}
