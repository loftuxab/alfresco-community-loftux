package org.alfresco.share.util.httpCore.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: aliaksei.bul
 * Date: 06.06.13
 * Time: 17:15
 */
public class EmulatorException extends RuntimeException {

    public EmulatorException() {
        super();
    }

    public EmulatorException(String message) {
        super(message);
    }

    public EmulatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmulatorException(Throwable cause) {
        super(cause);
    }


}
