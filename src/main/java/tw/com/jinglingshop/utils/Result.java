package tw.com.jinglingshop.utils;

import lombok.Data;

/**
 * ClassName:Result
 * Package:tw.com.myispan.utils
 * Description:
 *
 * @Author chiu
 * @Create 2023/7/25 上午 10:13
 * @Version 1.0
 */
@Data
public class Result {
    // 狀態碼 1:成功 0:失敗
    private Integer code;
    // 提示訊息
    private String msg;
    // 返回數據
    private Object data;

    public Result() {
    }

    public Result(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result success() {
        return new Result(1, "sucess", null);
    }

    public static Result success(Object data) {
        return new Result(1, "sucess", data);
    }

    public static Result success(String msg) {
        return new Result(1, msg, null);
    }

    public static Result success(String msg, Object data) {
        return new Result(1, msg, data);
    }

    public static Result error() {
        return new Result(0, "error", null);
    }

    public static Result error(String msg) {
        return new Result(0, msg, null);
    }

    public static Result error(Object data) {
        return new Result(0, "error", null);
    }

    public static Result error(String msg, Object data) {
        return new Result(0, msg, data);
    }

}
