package model;

/**
 * Created by SEELE on 2016/11/8.
 */

public class BodyResponse<T> {
    public String code;
    public String message;
    public T data;

    @Override
    public String toString() {
        return "BodyResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
