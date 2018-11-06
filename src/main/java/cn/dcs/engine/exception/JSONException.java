package cn.dcs.engine.exception;

public class JSONException extends Exception {

    private static final long serialVersionUID = 5483926386620653406L;

    public JSONException(String message) {
        super(message);
    }

    public JSONException(Throwable cause) {
        super(cause);
    }

    public JSONException(String message, Throwable cause) {
        super(message, cause);
    }
}
