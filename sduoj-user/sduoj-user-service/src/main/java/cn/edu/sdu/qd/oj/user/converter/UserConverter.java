package cn.edu.sdu.qd.oj.user.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import cn.edu.sdu.qd.oj.user.entity.UserDO;

@org.mapstruct.Mapper(componentModel = "spring")
public interface UserConverter extends BaseConverter<UserDO, UserDTO> {
}
