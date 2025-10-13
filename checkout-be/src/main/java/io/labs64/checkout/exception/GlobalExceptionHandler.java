package io.labs64.checkout.exception;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.labs64.checkout.messages.ValidationMessages;
import io.labs64.checkout.v1.model.ErrorCode;
import io.labs64.checkout.v1.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {
    private final ValidationMessages msg;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(final MethodArgumentNotValidException ex,
            final HttpServletRequest request) {
        String message = msg.failed();

        final FieldError firstError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);

        if (firstError != null) {
            message = msg.invalidField(firstError.getField(), firstError.getDefaultMessage());
        }

        final ErrorResponse error = buildErrorResponse(ErrorCode.VALIDATION_ERROR, message, request);

        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleCustomValidation(final ValidationException ex,
            final HttpServletRequest request) {
        final String message = msg.invalidField(ex.getField(), ex.getMessage());
        final ErrorResponse error = buildErrorResponse(ex.getErrorCode(), message, request);

        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolations(final ConstraintViolationException ex,
            final HttpServletRequest req) {
        final String message = ex.getConstraintViolations().stream().findFirst()
                .map(v -> msg.invalidField(String.valueOf(v.getPropertyPath()), v.getMessage())).orElse(msg.failed());

        final ErrorResponse error = buildErrorResponse(ErrorCode.VALIDATION_ERROR, message, req);

        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(final ApiException ex, final HttpServletRequest request) {
        final ErrorResponse error = buildErrorResponse(ex.getErrorCode(), ex.getMessage(), request);
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(final Exception ex, final HttpServletRequest request) {
        final ErrorResponse error = buildErrorResponse(ErrorCode.INTERNAL_ERROR, "Unexpected error", request);

        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private ErrorResponse buildErrorResponse(final ErrorCode code, final String message,
            final HttpServletRequest request) {
        final String traceId = request.getHeader("X-Request-ID");

        final ErrorResponse error = new ErrorResponse();
        error.setCode(code);
        error.setMessage(message);
        error.setTraceId(traceId);
        error.setTimestamp(OffsetDateTime.now());

        return error;
    }
}