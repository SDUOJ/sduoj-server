/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.common.pojo;

import cn.edu.sdu.qd.oj.common.enums.AcceptedEnum;
import cn.edu.sdu.qd.oj.common.enums.ExceptionEnum;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @ClassName ResponseResult
 * @Description 反馈结果类
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@Getter
@ToString
public class ResponseResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private long timestamp;
    private Object data;

    private ResponseResult() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应，但无响应数据。
     */
    public static ResponseResult ok() {
        return ok(null);
    }

    /**
     * 错误响应，但无响应数据。
     */
    public static ResponseResult error(ExceptionEnum em) {
        return new ResponseResult()
                .setCode(em.code)
                .setMessage(em.message);
    }

    /**
     * 异常反馈，需要客户端处理的
     */
    public static ResponseResult error() {
        return error(null);
    }

    /**
     * 成功响应，存在响应数据。
     */
    public static ResponseResult ok(Object data) {
        return new ResponseResult()
                .setCode(AcceptedEnum.OK.code)
                .setMessage(AcceptedEnum.OK.message)
                .setData(data);
    }

    /**
     * 错误响应，存在响应数据。
     */
    public static ResponseResult error(Object data) {
        return new ResponseResult()
                .setCode(AcceptedEnum.ERROR.code)
                .setMessage(AcceptedEnum.ERROR.message)
                .setData(data);
    }

    public ResponseResult setData(Object data) {
        this.data = data;
        return this;
    }

    public ResponseResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public ResponseResult setCode(int code) {
        this.code = code;
        return this;
    }
}