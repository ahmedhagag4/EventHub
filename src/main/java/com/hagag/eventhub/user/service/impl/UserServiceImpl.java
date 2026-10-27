package com.hagag.eventhub.user.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hagag.eventhub.dto.*;
import com.hagag.eventhub.entity.*;
import com.hagag.eventhub.repository.*;
import lombok.RequiredArgsConstructor;
import com.hagag.eventhub.constants.ApplicationConstants;

import com.hagag.eventhub.user.service.IUserService;
import com.hagag.eventhub.util.ApplicationUtility;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements IUserService {

    private final EventHubUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizerRepository organizerRepository;
    private final ProfileRepository profileRepository;
    private final EventRepository eventRepository;
    private final EventApplicationRepository eventApplicationRepository;

    @Override
    public Optional<UserDto> searchUserByEmail(String email) {
        return userRepository.findEventHubUserByEmail(email)
                .map(this::mapToUserDto);
    }

    @Transactional
    @Override
    public UserDto elevateToManager(Long userId) {
        EventHubUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Check if user is already an manager
        if (ApplicationConstants.ROLE_EMPLOYER.equals(user.getRole().getName())) {
            return mapToUserDto(user);
        }
        // Check if user is already an admin
        if (ApplicationConstants.ROLE_ADMIN.equals(user.getRole().getName())) {
            throw new RuntimeException("Cannot elevate admin user to manager role");
        }

        // Find ROLE_EMPLOYER
        Role managerRole = roleRepository.findRoleByName(ApplicationConstants.ROLE_EMPLOYER)
                .orElseThrow(() -> new RuntimeException("ROLE_EMPLOYER not found"));
        user.setRole(managerRole);
        // EventHubUser updatedUser = userRepository.save(user);
        /**
         * Why it’s unnecessary
         * findById() returns a managed entity
         * You modify it inside a transaction
         * Dirty checking automatically updates it
         */
        return mapToUserDto(user);
    }

    @Transactional
    @Override
    public UserDto assignOrganizerToManager(Long userId, Long organizerId) {
        EventHubUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        // Verify user is a manager
        if (!ApplicationConstants.ROLE_EMPLOYER.equals(user.getRole().getName())) {
            throw new RuntimeException("User must be an manager to be assigned to a organizer");
        }
        Organizer organizer = organizerRepository.findById(String.valueOf(organizerId))
                .orElseThrow(() -> new RuntimeException("Organizer not found with ID: " + organizerId));
        user.setOrganizer(organizer);
        // EventHubUser updatedUser = userRepository.save(user);
        return mapToUserDto(user);
    }

    @Transactional
    @Override
    public ProfileDto createOrUpdateProfile(String userEmail, String profileJson,
                                            MultipartFile profilePicture, MultipartFile resume) throws JsonProcessingException {
        EventHubUser user = userRepository.findEventHubUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        Profile profile = user.getProfile();
        if (null == profile) {
            profile = new Profile();
            profile.setUser(user);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        // Parse JSON string to ProfileDto
        ProfileDto profileDto = objectMapper.readValue(profileJson, ProfileDto.class);
        Profile savedProfile = profileRepository.save(mapToProfile(profile, profileDto, profilePicture, resume));
        return mapToProfileDto(savedProfile, false);
    }

    @Override
    public ProfileDto getProfile(String userEmail) {
        EventHubUser user = userRepository.findEventHubUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        if (user.getProfile() == null) {
            return null;
        }
        return mapToProfileDto(user.getProfile(), false);
    }

    @Override
    public ProfileDto getProfilePicture(String userEmail) {
        EventHubUser user = userRepository.findEventHubUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        if (user.getProfile() == null) {
            return null;
        }
        return mapToProfileDto(user.getProfile(), true);
    }

    @Override
    public ProfileDto getResume(String userEmail) {
        EventHubUser user = userRepository.findEventHubUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        if (user.getProfile() == null) {
            return null;
        }
        return mapToProfileDto(user.getProfile(), true);
    }

    @Transactional
    @Override
    public EventDto saveEvent(String userEmail, Long eventId) {
        // Validate if user exists
        EventHubUser user = userRepository.findEventHubUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        // Validate event exists
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
        user.getSavedEvents().add(event);
        // userRepository.save(user);
        return ApplicationUtility.transformEventToDto(event);
    }

    @Transactional
    @Override
    public void unsaveEvent(String userEmail, Long eventId) {
        // Validate if user exists
        EventHubUser user = userRepository.findEventHubUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        // Validate event exists
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
        user.getSavedEvents().remove(event);
    }

    @Override
    public List<EventDto> getSavedEvents(String userEmail) {
        // Validate if user exists
        EventHubUser user = userRepository.findEventHubUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        return user.getSavedEvents().stream().map(event -> ApplicationUtility.transformEventToDto(event))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventApplicationDto applyForEvent(String userEmail, ApplyEventRequestDto applyEventRequestDto) {
        // Validate if user exists
        EventHubUser user = userRepository.findEventHubUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        Long eventId = applyEventRequestDto.eventId();
        if (eventApplicationRepository.existsByUserIdAndEventId(user.getId(), eventId)) {
            throw new RuntimeException("You have already applied for this event");
        }
        // Validate event exists
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
        // Create event application
        EventApplication application = new EventApplication();
        application.setUser(user);
        application.setEvent(event);
        application.setAppliedAt(Instant.now());
        application.setStatus(ApplicationConstants.PENDING);
        application.setCoverLetter(applyEventRequestDto.coverLetter());
        EventApplication saved = eventApplicationRepository.save(application);
        // Increment applications count
        event.setApplicationsCount(event.getApplicationsCount() != null ? event.getApplicationsCount() + 1 : 1);
        // eventRepository.save(event); - Optional
        return ApplicationUtility.mapToEventApplicationDto(saved);
    }

    @Transactional
    @Override
    public void withdrawApplication(String userEmail, Long eventId) {
        // Validate if user exists
        EventHubUser user = userRepository.findEventHubUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        if (!eventApplicationRepository.existsByUserIdAndEventId(user.getId(), eventId)) {
            throw new RuntimeException("You have not applied for this event");
        }
        eventApplicationRepository.deleteByUserIdAndEventId(user.getId(), eventId);
        // Get the event to update the count
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        // Decrement applications count (ensure it doesn't go below 0)
        if (event.getApplicationsCount() != null && event.getApplicationsCount() > 0) {
            event.setApplicationsCount(event.getApplicationsCount() - 1);
            // eventRepository.save(event); - Optional
        }
    }

    @Override
    public List<EventApplicationDto> getEventSeekerApplications(String userEmail) {
        // Validate if user exists
        EventHubUser user = userRepository.findEventHubUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        return user.getEventApplications().stream().map(eventApp ->
                        ApplicationUtility.mapToEventApplicationDto(eventApp))
                .collect(Collectors.toList());
    }

    private Profile mapToProfile(Profile profile, ProfileDto profileDto,
            MultipartFile profilePicture, MultipartFile resume) {
        // Update text fields
        profile.setEventTitle(profileDto.eventTitle());
        profile.setLocation(profileDto.location());
        profile.setExperienceLevel(profileDto.experienceLevel());
        profile.setProfessionalBio(profileDto.professionalBio());
        profile.setPortfolioWebsite(profileDto.portfolioWebsite());
        // Handle profile picture upload
        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                profile.setProfilePicture(profilePicture.getBytes());
                profile.setProfilePictureName(profilePicture.getOriginalFilename());
                profile.setProfilePictureType(profilePicture.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload profile picture", e);
            }
        }
        // Handle resume upload
        if (resume != null && !resume.isEmpty()) {
            try {
                profile.setResume(resume.getBytes());
                profile.setResumeName(resume.getOriginalFilename());
                profile.setResumeType(resume.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload resume", e);
            }
        }
        return profile;
    }

    private ProfileDto mapToProfileDto(Profile profile, boolean includeBinaryData) {
        ProfileDto dto;
        if (includeBinaryData) {
            dto = new ProfileDto(profile.getId(), profile.getUser().getId(),
                    profile.getEventTitle(), profile.getLocation(), profile.getExperienceLevel(),
                    profile.getProfessionalBio(), profile.getPortfolioWebsite(), profile.getProfilePicture(),
                    profile.getProfilePictureName(), profile.getProfilePictureType(), profile.getResume(),
                    profile.getResumeName(), profile.getResumeType(), profile.getCreatedAt(), profile.getUpdatedAt()
            );
        } else {
            dto = new ProfileDto(profile.getId(), profile.getUser().getId(),
                    profile.getEventTitle(), profile.getLocation(), profile.getExperienceLevel(),
                    profile.getProfessionalBio(), profile.getPortfolioWebsite(), null,
                    profile.getProfilePictureName(), profile.getProfilePictureType(), null,
                    profile.getResumeName(), profile.getResumeType(), profile.getCreatedAt(), profile.getUpdatedAt());
        }
        return dto;
    }

    private UserDto mapToUserDto(EventHubUser user) {
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user, dto);
        dto.setUserId(user.getId());
        dto.setRole(user.getRole() != null ? user.getRole().getName() : null);
        dto.setOrganizerId(user.getOrganizer() != null ? user.getOrganizer().getId() : null);
        dto.setOrganizerName(user.getOrganizer() != null ? user.getOrganizer().getName() : null);
        return dto;
    }
}
