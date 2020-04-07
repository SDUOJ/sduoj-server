/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.checkpoint.service;

import cn.edu.sdu.qd.oj.checkpoint.mapper.CheckpointMapper;
import cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName checkpointManageService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:28
 * @Version V1.0
 **/

@Service
public class CheckpointManageService {

    @Autowired
    private CheckpointMapper checkpointMapper;

    public Checkpoint queryById(long checkpointId) {
        return checkpointMapper.selectByPrimaryKey(checkpointId);
    }
}