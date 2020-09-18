/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName PageResult
 * @Description 页结果类
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private Long total;
    private Long totalPage;
    private List<T> rows;

    public PageResult(Long totalPage, List<T> rows) {
        this.totalPage = totalPage;
        this.rows = rows;
        if (rows != null)
            this.total = (long) rows.size();
        else
            this.total = 0L;
    }

    public PageResult(Integer totalPage, List<T> rows) {
        this((long) totalPage, rows);
    }
}
