package cn.edu.sdu.qd.oj.contest.converter;

import cn.edu.sdu.qd.oj.contest.dto.ContestListDTO;
import cn.edu.sdu.qd.oj.contest.entity.ContestListDO;

@org.mapstruct.Mapper(
        componentModel = "spring",
        imports = {ContestConvertUtils.class}
)
public interface ContestListConverter extends BaseContestConverter<ContestListDO, ContestListDTO> {

    @org.mapstruct.Mapping(
            target = "username",
            expression = "java( ContestConvertUtils.userIdToUsername(source.getUserId()) )"
    )
    ContestListDTO to(ContestListDO source);


}
