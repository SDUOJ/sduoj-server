package cn.edu.sdu.qd.oj.contest.converter;

import cn.edu.sdu.qd.oj.contest.dto.ContestCreateReqDTO;
import cn.edu.sdu.qd.oj.contest.entity.ContestDO;

import java.util.List;
import java.util.Optional;

@org.mapstruct.Mapper(
        componentModel = "spring",
        imports = {Optional.class, List.class}
)
public interface ContestCreateReqConverter extends BaseContestConverter<ContestDO, ContestCreateReqDTO> {

    @org.mapstruct.Mapping(
            target = "participantNum",
            expression = "java( Optional.ofNullable(source.getParticipants()).map(List::size).orElse(0) )"
    )
    ContestDO from(ContestCreateReqDTO source);


}
