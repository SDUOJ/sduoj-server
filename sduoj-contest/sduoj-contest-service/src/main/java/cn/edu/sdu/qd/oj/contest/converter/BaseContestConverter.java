package cn.edu.sdu.qd.oj.contest.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.common.util.SpringContextUtils;
import cn.edu.sdu.qd.oj.common.util.UserCacheUtils;
import cn.edu.sdu.qd.oj.contest.dto.ContestProblemListDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


@org.mapstruct.MapperConfig
public interface BaseContestConverter<S, T> extends BaseConverter<S, T> {

    default List<ContestProblemListDTO> problemsTo(String problems) {
        return ContestConvertUtils.problemsTo(problems);
    }

    default String problemsFrom(List<ContestProblemListDTO> contestProblemListDTOList) {
        return ContestConvertUtils.problemsFrom(contestProblemListDTOList);
    }

    default List<String> participantsTo(byte[] participants) {
        return ContestConvertUtils.participantsTo(participants);
    }

    default byte[] participantsFrom(List<String> participants) {
        return ContestConvertUtils.participantsFrom(participants);
    }
}