package com.fly.demo.common;

/**
 * 统一信息处理结果的包装
 */
public enum ResultCode {
    SUCCESS(0, "操作成功"),
    FAILED(1000, "操作失败"),
    FAILLED_UNAUTHORIZED(1001, "未授权"),
    FAILED_PARAMS_VALIDATE(1002, "参数校验失败"),
    FAILED_FORBIDDEN(1003, "禁止访问"),
    FAILED_CREATE(1004, "新增失败"),
    FAILED_NOT_EXISTS(1005, "资源不存在"),
    FAILED_USER_EXISTS(1101, "用户已存在"),
    FAILED_USER_NOT_EXISTS(1102, "用户不存在"),
    FAILED_LOGIN(1103, "用户或密码错误"),
    FAILED_USER_BANNED(1104, "您已被禁言，请联系管理员，并重新登陆."),
    FAILED_TOW_PWD_NOT_SAME(1105, "两次输入的密码不一致"),
    ERROR_SERVICES(2000, "服务器内部错误"),
    ERROR_IS_NULL(2001, "IS NULL.");
    int code; // 状态码
    String message; // 描述信息

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "code = " + code + ", message = " + message;
    }
}
