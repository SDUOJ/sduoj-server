package cn.edu.sdu.qd.oj.submit.dao;

import cn.edu.sdu.qd.oj.submit.entity.SubmissionDO;
import cn.edu.sdu.qd.oj.submit.mapper.SubmissionMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class SubmissionDao extends ServiceImpl<SubmissionMapper, SubmissionDO> {
}
