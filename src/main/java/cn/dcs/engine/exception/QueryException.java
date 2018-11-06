package cn.dcs.engine.exception;

public class QueryException extends RuntimeException {

    private static final long serialVersionUID = 5483926386620653406L;

    public QueryException(String message) {
        super(message);
    }

    public QueryException(Throwable cause) {
        super(cause);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
