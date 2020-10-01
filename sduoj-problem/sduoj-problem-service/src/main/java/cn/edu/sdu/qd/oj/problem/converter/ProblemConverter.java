package cn.edu.sdu.qd.oj.problem.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDescriptionDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDescriptionDO;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.mapstruct.Mapper(componentModel = "spring")
public interface ProblemConverter extends BaseConverter<ProblemDO, ProblemDTO> {

    ProblemDescriptionConverter problemDescriptionConverter = Mappers.getMapper(ProblemDescriptionConverter.class);

    ProblemDescriptionListConverter problemDescriptionListConverter = Mappers.getMapper(ProblemDescriptionListConverter.class);

    default ProblemDTO to(ProblemDO problemDO,
                          ProblemDescriptionDO problemDescriptionDO,
                          List<ProblemDescriptionDO> problemDescriptionDOList) {
        ProblemDTO problemDTO = to(problemDO);
        problemDTO.setProblemDescriptionDTO(problemDescriptionConverter.to(problemDescriptionDO));
        problemDTO.setProblemDescriptionListDTOList(problemDescriptionListConverter.to(problemDescriptionDOList));
        return problemDTO;
    }
}
