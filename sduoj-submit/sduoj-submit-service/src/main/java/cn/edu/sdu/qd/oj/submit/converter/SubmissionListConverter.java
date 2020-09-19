package cn.edu.sdu.qd.oj.submit.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListDTO;
import cn.edu.sdu.qd.oj.submit.entity.SubmissionDO;

@org.mapstruct.Mapper(componentModel = "spring")
public interface SubmissionListConverter extends BaseConverter<SubmissionDO, SubmissionListDTO> {
}
