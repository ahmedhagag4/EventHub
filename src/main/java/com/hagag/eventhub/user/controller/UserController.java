package com.hagag.eventhub.user.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.hagag.eventhub.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.hagag.eventhub.user.service.IUserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/search/admin")
    public ResponseEntity<?> searchUserByEmail(@RequestParam String email) {
        Optional<UserDto> userOptional = userService.searchUserByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found with email: " + email));
        }
        return ResponseEntity.ok(userOptional.get());
    }

    @PatchMapping("/{userId}/role/manager/admin")
    public ResponseEntity<?> elevateToManager(@PathVariable Long userId) {
        UserDto updatedUser = userService.elevateToManager(userId);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{userId}/organizer/{organizerId}/admin")
    public ResponseEntity<?> assignOrganizerToManager(
            @PathVariable Long userId, @PathVariable Long organizerId) {
        UserDto updatedUser = userService.assignOrganizerToManager(userId, organizerId);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping(value = "/profile/attendee", version = "1.0",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileDto> createOrUpdateProfile(
            @RequestPart(value = "profile") String profileJson,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestPart(value = "resume", required = false) MultipartFile resume,
            Authentication authentication) throws JsonProcessingException {
        String userEmail = authentication.getName();
        ProfileDto savedProfile = userService.createOrUpdateProfile(
                userEmail, profileJson, profilePicture, resume);
        return ResponseEntity.ok(savedProfile);
    }

    @GetMapping(value = "/profile/attendee", version = "1.0")
    public ResponseEntity<ProfileDto> getProfile(Authentication authentication) {
        String userEmail = authentication.getName();
        ProfileDto profileDto = userService.getProfile(userEmail);
        return ResponseEntity.ok(profileDto);
    }

    @GetMapping(value = "/profile/picture/attendee", version = "1.0")
    public ResponseEntity<byte[]> getProfilePicture(Authentication authentication) {
        String userEmail = authentication.getName();
        com.hagag.eventhub.dto.ProfileDto profileDto = userService.getProfilePicture(userEmail);
        byte[] picture = profileDto.profilePicture();
        if (picture == null || picture.length == 0) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(profileDto.profilePictureType()));
        headers.setContentLength(picture.length);
        return new ResponseEntity<>(picture, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/profile/resume/attendee", version = "1.0")
    public ResponseEntity<byte[]> getResume(Authentication authentication) {
        String userEmail = authentication.getName();
        com.hagag.eventhub.dto.ProfileDto profileDto = userService.getResume(userEmail);
        byte[] resume = profileDto.resume();
        if (resume == null || resume.length == 0) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(profileDto.resumeType()));
        headers.setContentLength(resume.length);
        headers.setContentDispositionFormData("attachment", profileDto.resumeName());
        return new ResponseEntity<>(resume, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/saved-events/{eventId}/attendee", version = "1.0")
    public ResponseEntity<EventDto> saveEvent(@PathVariable Long eventId,
                                              Authentication authentication) {
        String userEmail = authentication.getName();
        EventDto savedEvent = userService.saveEvent(userEmail, eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    @DeleteMapping(value = "/saved-events/{eventId}/attendee", version = "1.0")
    public ResponseEntity<String> unsaveEvent(@PathVariable Long eventId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        userService.unsaveEvent(userEmail, eventId);
        return ResponseEntity.status(HttpStatus.OK).body("Event unsaved successfully");
    }

    @GetMapping(value = "/saved-events/attendee", version = "1.0")
    public ResponseEntity<List<EventDto>> getSavedEvents(Authentication authentication) {
        String userEmail = authentication.getName();
        List<EventDto> savedEventDtos = userService.getSavedEvents(userEmail);
        return ResponseEntity.ok(savedEventDtos);
    }
	
    @PostMapping(value = "/event-applications/attendee", version = "1.0")
    public ResponseEntity<EventApplicationDto> applyForEvent(
            @RequestBody @Valid ApplyEventRequestDto applyEventRequestDto, Authentication authentication) {
        String userEmail = authentication.getName();
        EventApplicationDto application = userService.applyForEvent(userEmail, applyEventRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(application);
    }

    @DeleteMapping(value = "/event-applications/{eventId}/attendee", version = "1.0")
    public ResponseEntity<String> withdrawApplication(@PathVariable Long eventId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        userService.withdrawApplication(userEmail, eventId);
        return ResponseEntity.status(HttpStatus.OK).body("Application withdrawn successfully");
    }

    @GetMapping(value = "/event-applications/attendee", version = "1.0")
    public ResponseEntity<List<EventApplicationDto>> getEventSeekerApplications(Authentication authentication) {
        String userEmail = authentication.getName();
        List<EventApplicationDto> applications = userService.getEventSeekerApplications(userEmail);
        return ResponseEntity.ok(applications);
    }
}
