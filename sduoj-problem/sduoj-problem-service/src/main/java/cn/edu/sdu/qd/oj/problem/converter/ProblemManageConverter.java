package cn.edu.sdu.qd.oj.problem.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.problem.dto.ProblemManageDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;

@org.mapstruct.Mapper(componentModel = "spring")
public interface ProblemManageConverter extends BaseConverter<ProblemDO, ProblemManageDTO> {
}
