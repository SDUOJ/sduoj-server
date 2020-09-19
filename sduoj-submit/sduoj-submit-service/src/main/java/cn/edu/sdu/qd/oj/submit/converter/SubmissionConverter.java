package cn.edu.sdu.qd.oj.submit.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionDTO;
import cn.edu.sdu.qd.oj.submit.entity.SubmissionDO;

@org.mapstruct.Mapper(componentModel = "spring")
public interface SubmissionConverter extends BaseConverter<SubmissionDO, SubmissionDTO> {
}
