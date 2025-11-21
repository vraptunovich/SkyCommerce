package com.rvk.skycommerce.api.error;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ApiErrorResponse(Instant timestamp, int status, String error, String message, String path,
                               List<FieldError> fieldErrors) {

    @Builder
    public record FieldError(String field, String message) {
    }
}
