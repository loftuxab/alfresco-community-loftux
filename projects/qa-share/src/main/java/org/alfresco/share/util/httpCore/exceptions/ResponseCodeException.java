package org.alfresco.share.util.httpCore.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: aliaksei.bul
 * Date: 24.06.13
 * Time: 18:00
 */
public class ResponseCodeException extends RuntimeException {

    public ResponseCodeException() {
        super();
    }

    public ResponseCodeException(String message) {
        super(message);
    }

    public ResponseCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseCodeException(Throwable cause) {
        super(cause);
    }
}
