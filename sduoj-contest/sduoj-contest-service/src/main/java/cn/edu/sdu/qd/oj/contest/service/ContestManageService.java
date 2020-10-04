package cn.edu.sdu.qd.oj.contest.service;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.contest.converter.ContestCreateReqConverter;
import cn.edu.sdu.qd.oj.contest.dao.ContestDao;
import cn.edu.sdu.qd.oj.contest.dto.ContestCreateReqDTO;
import cn.edu.sdu.qd.oj.contest.entity.ContestDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContestManageService {

    @Autowired
    private ContestDao contestDao;

    @Autowired
    private ContestCreateReqConverter contestCreateReqConverter;

    public Long create(ContestCreateReqDTO reqDTO) {
        ContestDO contestDO = contestCreateReqConverter.from(reqDTO);
        if (!contestDao.save(contestDO)) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        return contestDO.getContestId();
    }


}
