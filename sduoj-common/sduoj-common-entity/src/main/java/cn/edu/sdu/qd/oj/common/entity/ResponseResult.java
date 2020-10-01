/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.common.entity;

import cn.edu.sdu.qd.oj.common.enums.AcceptedEnum;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.enums.HttpStatus;
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
public class ResponseResult <T> implements Serializable {
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
	 * 错误响应，需要客户端处理的
	 */
	public static ResponseResult fail(ApiExceptionEnum em) {
		return new ResponseResult()
				.setCode(em.code)
				.setMessage(em.message);
	}

	/**
	 * 错误响应，需要客户端处理的
	 */
	public static ResponseResult fail(int code, String message) {
		return new ResponseResult()
				.setCode(code)
				.setMessage(message);
	}

	/**
	 * 错误响应，存在响应数据。
	 */
	public static <T> ResponseResult<T> fail(HttpStatus status) {
		return new ResponseResult()
				.setCode(status.value())
				.setMessage(status.getReasonPhrase())
				.setData(null);
	}

	/**
	 * 错误响应，存在响应数据。
	 */
	public static <T> ResponseResult<T> fail(T data) {
		return new ResponseResult()
				.setCode(AcceptedEnum.ERROR.code)
				.setMessage(AcceptedEnum.ERROR.message)
				.setData(data);
	}


	/**
	 * 异常反馈，需要服务端处理的
	 */
	public static ResponseResult error(ApiExceptionEnum em) {
		return fail(em);
	}

	/**
	 * 异常反馈，需要服务端处理的
	 */
	public static ResponseResult error() {
		return new ResponseResult()
				.setCode(AcceptedEnum.ERROR.code)
				.setMessage(AcceptedEnum.ERROR.message);
	}


	/**
	 * 成功响应，存在响应数据。
	 */
	public static <T> ResponseResult<T> ok(T data) {
		return new ResponseResult()
				.setCode(AcceptedEnum.OK.code)
				.setMessage(AcceptedEnum.OK.message)
				.setData(data);
	}



	public ResponseResult setData(T data) {
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