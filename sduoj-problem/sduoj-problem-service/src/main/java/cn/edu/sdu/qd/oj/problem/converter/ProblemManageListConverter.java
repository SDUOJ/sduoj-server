package cn.edu.sdu.qd.oj.problem.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.problem.dto.ProblemManageListDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemManageListDO;

@org.mapstruct.Mapper(componentModel = "spring")
public interface ProblemManageListConverter extends BaseConverter<ProblemManageListDO, ProblemManageListDTO> {
}
