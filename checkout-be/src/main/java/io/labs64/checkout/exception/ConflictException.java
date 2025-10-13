package io.labs64.checkout.exception;

import org.springframework.http.HttpStatus;

import io.labs64.checkout.v1.model.ErrorCode;

public class ConflictException extends ApiException {
    private final static ErrorCode ERROR_CODE = ErrorCode.CONFLICT;
    private final static HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;

    public ConflictException(final String message) {
        super(HTTP_STATUS, ERROR_CODE, message);
    }

    public ConflictException(final String message, final Throwable cause) {
        super(HTTP_STATUS, ERROR_CODE, message, cause);
    }
}
