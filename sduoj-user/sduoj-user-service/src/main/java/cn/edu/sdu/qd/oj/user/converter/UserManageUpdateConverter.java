package cn.edu.sdu.qd.oj.user.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.user.dto.UserManageUpdateReqDTO;
import cn.edu.sdu.qd.oj.user.entity.UserDO;

@org.mapstruct.Mapper(componentModel = "spring")
public interface UserManageUpdateConverter extends BaseConverter<UserDO, UserManageUpdateReqDTO> {
}
