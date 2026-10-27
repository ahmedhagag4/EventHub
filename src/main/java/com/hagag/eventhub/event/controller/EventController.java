package com.hagag.eventhub.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.hagag.eventhub.dto.EventApplicationDto;
import com.hagag.eventhub.dto.EventDto;
import com.hagag.eventhub.dto.UpdateEventApplicationDto;
import com.hagag.eventhub.event.service.IEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final IEventService EventService;

    @GetMapping(path = "/manager", version = "1.0")
    public ResponseEntity<List<EventDto>> getManagerEvents(Authentication authentication) {
        String managerEmail = authentication.getName();
        List<EventDto> Events = EventService.getManagerEvents(managerEmail);
        return ResponseEntity.ok(Events);
    }

    @PostMapping(path = "/manager", version = "1.0")
    public ResponseEntity<EventDto> createEvent(@RequestBody @Valid EventDto EventDto, Authentication authentication) {
        String managerEmail = authentication.getName();
        EventDto createdEvent = EventService.createEvent(EventDto, managerEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);

    }

    @PatchMapping("/{EventId}/status/manager")
    public ResponseEntity<?> updateEventStatus(
            @PathVariable Long EventId,
            @RequestBody Map<String, String> requestBody,
            Authentication authentication) {
        String managerEmail = authentication.getName();
        String status = requestBody.get("status");

        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Status is required"));
        }
        EventDto updatedEvent = EventService.updateEventStatus(EventId, status.toUpperCase(), managerEmail);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/applications/{EventId}/manager")
    public ResponseEntity<List<EventApplicationDto>> getApplicationsByEventForManager(
            @PathVariable Long EventId) {
        List<EventApplicationDto> applications = EventService.getApplicationsByEventForManager(EventId);
        return ResponseEntity.ok(applications);
    }

    @PatchMapping("/applications/manager")
    public ResponseEntity<String> updateEventApplication(
            @RequestBody @Valid UpdateEventApplicationDto updateEventApplicationDto) {
        boolean isUpdated = EventService.updateEventApplication(updateEventApplicationDto);
        if(!isUpdated) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update application");
        }
        return ResponseEntity.ok("Application updated successfully");
    }
}
