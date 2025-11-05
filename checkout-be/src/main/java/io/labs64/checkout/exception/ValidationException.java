package io.labs64.checkout.exception;

import org.springframework.http.HttpStatus;

import io.labs64.checkout.model.ErrorCode;
import lombok.Getter;

@Getter
public class ValidationException extends ApiException {
    private final static ErrorCode ERROR_CODE = ErrorCode.VALIDATION_ERROR;
    private final static HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    private final String field;

    public ValidationException(final String message) {
        super(HTTP_STATUS, ERROR_CODE, message);
        this.field = null;
    }

    public ValidationException(final String field, final String message) {
        super(HTTP_STATUS, ERROR_CODE, message);
        this.field = field;
    }

    public ValidationException(final String field, final String message, final Throwable cause) {
        super(HTTP_STATUS, ERROR_CODE, message, cause);
        this.field = field;
    }
}
