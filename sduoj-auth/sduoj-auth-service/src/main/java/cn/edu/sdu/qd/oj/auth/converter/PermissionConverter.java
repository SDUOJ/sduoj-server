package cn.edu.sdu.qd.oj.auth.converter;

import cn.edu.sdu.qd.oj.auth.dto.PermissionDTO;
import cn.edu.sdu.qd.oj.auth.entity.PermissionDO;
import cn.edu.sdu.qd.oj.common.converter.BaseConverter;

@org.mapstruct.Mapper(componentModel = "spring")
public interface PermissionConverter extends BaseConverter<PermissionDO, PermissionDTO> {
}
