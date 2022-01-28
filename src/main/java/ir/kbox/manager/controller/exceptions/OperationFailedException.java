package ir.kbox.manager.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class OperationFailedException extends RuntimeException{
    public OperationFailedException() {
        super();
    }

    public OperationFailedException(String message) {
        super(message);
    }

    public OperationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperationFailedException(Throwable cause) {
        super(cause);
    }
}
