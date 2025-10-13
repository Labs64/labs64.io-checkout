package io.labs64.checkout.exception;

import org.springframework.http.HttpStatus;

import io.labs64.checkout.v1.model.ErrorCode;
import lombok.Getter;

@Getter
public class ConsentRequiredException extends ApiException {
    private final static ErrorCode ERROR_CODE = ErrorCode.CONSENT_REQUIRED;
    private final static HttpStatus HTTP_STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

    public ConsentRequiredException(final String message) {
        super(HTTP_STATUS, ERROR_CODE, message);
    }

    public ConsentRequiredException(final String message, final Throwable cause) {
        super(HTTP_STATUS, ERROR_CODE, message, cause);
    }
}
