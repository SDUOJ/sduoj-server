package cn.edu.sdu.qd.oj.user.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.user.dto.UserExtensionDTO;
import cn.edu.sdu.qd.oj.user.entity.UserExtensionDO;

@org.mapstruct.Mapper(componentModel = "spring")
public interface UserExtensionConverter extends BaseConverter<UserExtensionDO, UserExtensionDTO> {
}
