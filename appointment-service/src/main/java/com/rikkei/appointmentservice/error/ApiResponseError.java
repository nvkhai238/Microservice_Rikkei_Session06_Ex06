package com.rikkei.appointmentservice.error;

import java.time.Instant;

public record ApiResponseError(
        Instant timestamp,
        int status,
        String error,
        String message
) {
}

