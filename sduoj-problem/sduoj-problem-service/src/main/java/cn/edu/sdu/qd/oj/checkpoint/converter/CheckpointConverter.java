package cn.edu.sdu.qd.oj.checkpoint.converter;

import cn.edu.sdu.qd.oj.checkpoint.dto.CheckpointDTO;
import cn.edu.sdu.qd.oj.checkpoint.entity.CheckpointDO;
import cn.edu.sdu.qd.oj.common.converter.BaseConverter;

@org.mapstruct.Mapper(componentModel = "spring")
public interface CheckpointConverter extends BaseConverter<CheckpointDO, CheckpointDTO> {
}
