package com.hagag.eventhub.util;


import com.hagag.eventhub.constants.ApplicationConstants;
import com.hagag.eventhub.dto.EventApplicationDto;
import com.hagag.eventhub.dto.EventDto;
import com.hagag.eventhub.dto.ProfileDto;
import com.hagag.eventhub.entity.Event;
import com.hagag.eventhub.entity.EventApplication;
import com.hagag.eventhub.entity.EventHubUser;
import com.hagag.eventhub.entity.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ApplicationUtility {

    public static String getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return ApplicationConstants.SYSTEM;
        }
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof EventHubUser EventHubUser) {
            username = EventHubUser.getEmail();
        } else {
            username = principal.toString(); // fallback
        }
        return username;
    }

    public static EventDto transformEventToDto(Event Event) {
        return new EventDto(
                Event.getId(),
                Event.getTitle(),
                Event.getOrganizer().getId(),
                Event.getOrganizer().getName(),
                Event.getOrganizer().getLogo(),
                Event.getLocation(),
                Event.getWorkType(),
                Event.getEventType(),
                Event.getCategory(),
                Event.getExperienceLevel(),
                Event.getSalaryMin(),
                Event.getSalaryMax(),
                Event.getSalaryCurrency(),
                Event.getSalaryPeriod(),
                Event.getDescription(),
                Event.getRequirements(),
                Event.getBenefits(),
                Event.getPostedDate(),
                Event.getApplicationDeadline(),
                Event.getApplicationsCount(),
                Event.getFeatured(),
                Event.getUrgent(),
                Event.getRemote(),
                Event.getStatus()
        );
    }

    public static EventApplicationDto mapToEventApplicationDto(EventApplication application) {
        // Map profile if exists
        ProfileDto profileDto = null;
        Profile profile = application.getUser().getProfile();
        if (profile != null) {
            profileDto = new ProfileDto(
                    profile.getId(),
                    profile.getUser().getId(),
                    profile.getEventTitle(),
                    profile.getLocation(),
                    profile.getExperienceLevel(),
                    profile.getProfessionalBio(),
                    profile.getPortfolioWebsite(),
                    profile.getProfilePicture(),
                    profile.getProfilePictureName(),
                    profile.getProfilePictureType(),
                    profile.getResume(),
                    profile.getResumeName(),
                    profile.getResumeType(),
                    profile.getCreatedAt(),
                    profile.getUpdatedAt()
            );
        }
        return new EventApplicationDto(
                application.getId(),
                application.getUser().getId(),
                application.getUser().getName(),
                application.getUser().getEmail(),
                application.getUser().getMobileNumber(),
                profileDto,
                ApplicationUtility.transformEventToDto(application.getEvent()),
                application.getAppliedAt(),
                application.getStatus(),
                application.getCoverLetter(),
                application.getNotes()
        );
    }
}
