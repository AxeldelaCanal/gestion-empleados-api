package com.axeldev.gestionempleados.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        int status,
        String error,
        String message,
        LocalDateTime timestamp,
        List<FieldError> fieldErrors
) {
    public record FieldError(String field, String message) {}

    public static ApiError of(int status, String error, String message) {
        return new ApiError(status, error, message, LocalDateTime.now(), null);
    }

    public static ApiError ofFieldErrors(int status, String error, String message, List<FieldError> fieldErrors) {
        return new ApiError(status, error, message, LocalDateTime.now(), fieldErrors);
    }
}
