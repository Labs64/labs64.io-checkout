package io.labs64.checkout.exception;

import java.io.Serial;

import org.springframework.http.HttpStatus;

import io.labs64.checkout.v1.model.ErrorCode;
import lombok.Getter;

@Getter
public abstract class ApiException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final HttpStatus status;
    private final ErrorCode errorCode;

    protected ApiException(final HttpStatus status, final ErrorCode errorCode, final String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    protected ApiException(final HttpStatus status, final ErrorCode errorCode, final String message,
            final Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }
}
