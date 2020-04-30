/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemManageBoMapper;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemManageListBoMapper;
import cn.edu.sdu.qd.oj.problem.pojo.ProblemManageBo;
import cn.edu.sdu.qd.oj.problem.pojo.ProblemManageListBo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @ClassName ProblemManageService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:31
 * @Version V1.0
 **/

@Service
public class ProblemManageService {
    @Autowired
    private ProblemManageBoMapper problemManageBoMapper;

    @Autowired
    private ProblemManageListBoMapper problemManageListBoMapper;

    public ProblemManageBo queryById(Integer problemId) {
        return this.problemManageBoMapper.selectByPrimaryKey(problemId);
    }

    public boolean createProblem(ProblemManageBo problem) {
        problem.setProblemId(null);
        if (this.problemManageBoMapper.insertSelective(problem) != 1) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        return true;
    }

    public PageResult<ProblemManageListBo> queryProblemByPage(int pageNow, int pageSize) {
        Example example = new Example(ProblemManageListBo.class);
        PageHelper.startPage(pageNow, pageSize);
        Page<ProblemManageListBo> pageInfo = (Page<ProblemManageListBo>) problemManageListBoMapper.selectByExample(example);
        return new PageResult<>(pageInfo.getPages(), pageInfo);
    }

    public void update(ProblemManageBo problem) {
        if (this.problemManageBoMapper.updateByPrimaryKeySelective(problem) != 1)
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
    }
}