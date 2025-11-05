package io.labs64.checkout.exception;

import org.springframework.http.HttpStatus;

import io.labs64.checkout.model.ErrorCode;

public class TenantRequiredException extends ApiException {
    private final static ErrorCode ERROR_CODE = ErrorCode.MISSING_TENANT_ID;
    private final static HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public TenantRequiredException(final String message) {
        super(HTTP_STATUS, ERROR_CODE, message);
    }

    public TenantRequiredException(final String message, final Throwable cause) {
        super(HTTP_STATUS, ERROR_CODE, message, cause);
    }
}
