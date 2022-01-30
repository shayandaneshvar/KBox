package ir.kbox.manager.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class UnacceptableRequestException extends RuntimeException {
    public UnacceptableRequestException() {
        super();
    }

    public UnacceptableRequestException(String message) {
        super(message);
    }

    public UnacceptableRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnacceptableRequestException(Throwable cause) {
        super(cause);
    }
}
