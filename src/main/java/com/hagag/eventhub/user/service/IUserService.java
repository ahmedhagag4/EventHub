package com.hagag.eventhub.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hagag.eventhub.dto.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    /**
     * Search for a user by email address
     * @param email the email address to search for
     * @return Optional containing UserDto if found
     */
    Optional<UserDto> searchUserByEmail(String email);

    /**
     * Elevate a user's role to ROLE_Manager
     * @param userId the ID of the user to elevate
     * @return updated UserDto
     */
    UserDto elevateToManager(Long userId);

    /**
     * Assign a organizer to a manager
     * @param userId the ID of the manager
     * @param organizerId the ID of the organizer to assign
     * @return updated UserDto
     */
    UserDto assignOrganizerToManager(Long userId, Long organizerId);

    ProfileDto createOrUpdateProfile(String userEmail, String profileJson,
                                     MultipartFile profilePicture, MultipartFile resume) throws JsonProcessingException;

    ProfileDto getProfile(String userEmail);

    ProfileDto getProfilePicture(String userEmail);

    ProfileDto getResume(String userEmail);
	
    EventDto saveEvent(String userEmail, Long eventId);

    void unsaveEvent(String userEmail, Long eventId);

    List<EventDto> getSavedEvents(String userEmail);

    EventApplicationDto applyForEvent(String userEmail, ApplyEventRequestDto request);

    void withdrawApplication(String userEmail, Long eventId);

    List<EventApplicationDto> getEventSeekerApplications(String userEmail);
}
