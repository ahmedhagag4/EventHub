package com.hagag.eventhub.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateEventApplicationDto(
        @NotNull(message = "Application ID is required")
        Long applicationId,
        @NotNull(message = "Status is required")
        EventApplicationStatus status,
        String notes
) {
}

