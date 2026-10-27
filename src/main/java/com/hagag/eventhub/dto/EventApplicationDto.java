package com.hagag.eventhub.dto;

import java.time.Instant;

public record EventApplicationDto(
        Long id,
        Long userId,
        String userName,
        String userEmail,
        String userMobileNumber,
        ProfileDto userProfile,
        EventDto Event,
        Instant appliedAt,
        String status,
        String coverLetter,
        String notes
) {
}
