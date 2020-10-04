package cn.edu.sdu.qd.oj.contest.converter;

import cn.edu.sdu.qd.oj.contest.dto.ContestDTO;
import cn.edu.sdu.qd.oj.contest.entity.ContestDO;
import cn.edu.sdu.qd.oj.common.util.SpringContextUtils;
import cn.edu.sdu.qd.oj.common.util.UserCacheUtils;

@org.mapstruct.Mapper(
        componentModel = "spring",
        imports = {ContestConvertUtils.class}
)
public interface ContestConverter extends BaseContestConverter<ContestDO, ContestDTO> {

    @org.mapstruct.Mapping(
            target = "username",
            expression = "java( ContestConvertUtils.userIdToUsername(source.getUserId()) )"
    )
    ContestDTO to(ContestDO source);


}
