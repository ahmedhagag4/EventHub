package com.hagag.eventhub.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public record EventDto(
        Long id,

        @NotBlank(message = "Event title is required")
        String title,

        Long organizerId,
        String organizerName,
        String organizerLogo,

        @NotBlank(message = "Event location is required")
        String location,

        @NotBlank(message = "Work type is required")
        String workType,

        @NotBlank(message = "Event type is required")
        String EventType,

        @NotBlank(message = "Category is required")
        String category,

        @NotBlank(message = "Experience level is required")
        String experienceLevel,

        @NotNull(message = "Minimum salary is required")
        @DecimalMin(value = "0.0", message = "Minimum salary must be positive")
        BigDecimal salaryMin,

        @NotNull(message = "Maximum salary is required")
        @DecimalMin(value = "0.0", message = "Maximum salary must be positive")
        BigDecimal salaryMax,

        @NotBlank(message = "Salary currency is required")
        String salaryCurrency,

        @NotBlank(message = "Salary period is required")
        String salaryPeriod,

        @NotBlank(message = "Event description is required")
        String description,

        String requirements,
        String benefits,
        Instant postedDate,
        Instant applicationDeadline,
        Integer applicationsCount,
        Boolean featured,
        Boolean urgent,
        Boolean remote,
        String status
) implements Serializable {
}
