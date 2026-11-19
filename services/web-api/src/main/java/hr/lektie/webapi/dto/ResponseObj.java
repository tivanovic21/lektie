package hr.lektie.webapi.dto;

/** Envelope for every response body: {"message": "...", "data": ...}. */
public class ResponseObj<T> {
    private String message;
    private T data;

    public ResponseObj() {
    }

    public ResponseObj(String message) {
        this.message = message;
        this.data = null;
    }

    public ResponseObj(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
