package com.hagag.eventhub.dto;

import jakarta.validation.constraints.NotNull;

public record ApplyEventRequestDto(
        @NotNull(message = "Event ID is required")
        Long eventId,

        String coverLetter
) {
}

