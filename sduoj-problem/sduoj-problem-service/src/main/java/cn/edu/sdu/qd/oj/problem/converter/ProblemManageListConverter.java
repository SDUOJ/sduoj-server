package cn.edu.sdu.qd.oj.problem.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.problem.dto.ProblemManageListDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;

@org.mapstruct.Mapper(componentModel = "spring")
public interface ProblemManageListConverter extends BaseConverter<ProblemDO, ProblemManageListDTO> {
}
