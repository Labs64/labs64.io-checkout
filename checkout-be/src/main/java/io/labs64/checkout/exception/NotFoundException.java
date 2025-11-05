package io.labs64.checkout.exception;

import org.springframework.http.HttpStatus;

import io.labs64.checkout.model.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundException extends ApiException {
    private final static ErrorCode ERROR_CODE = ErrorCode.NOT_FOUND;
    private final static HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public NotFoundException(final String message) {
        super(HTTP_STATUS, ERROR_CODE, message);
    }

    public NotFoundException(final String message, final Throwable cause) {
        super(HTTP_STATUS, ERROR_CODE, message, cause);
    }
}
