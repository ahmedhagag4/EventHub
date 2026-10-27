package com.hagag.eventhub.event.service.impl;

import lombok.RequiredArgsConstructor;
import com.hagag.eventhub.dto.EventApplicationDto;
import com.hagag.eventhub.dto.EventDto;
import com.hagag.eventhub.dto.UpdateEventApplicationDto;
import com.hagag.eventhub.entity.Event;
import com.hagag.eventhub.entity.EventApplication;
import com.hagag.eventhub.entity.EventHubUser;
import com.hagag.eventhub.event.service.IEventService;
import com.hagag.eventhub.repository.EventApplicationRepository;
import com.hagag.eventhub.repository.EventHubUserRepository;
import com.hagag.eventhub.repository.EventRepository;
import com.hagag.eventhub.util.ApplicationUtility;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService implements IEventService {

    private final EventRepository EventRepository;
    private final EventHubUserRepository userRepository;
    private final EventApplicationRepository EventApplicationRepository;

    @Override
    public List<EventDto> getManagerEvents(String managerEmail) {
        EventHubUser manager = userRepository.findEventHubUserByEmail(managerEmail)
                .orElseThrow(() -> new RuntimeException("manager not found"));

        if (manager.getOrganizer() == null) {
            throw new RuntimeException("manager does not have a Organizer assigned");
        }

        List<Event> Events = manager.getOrganizer().getEvents();
        return Events.stream()
                .map(Event -> ApplicationUtility.transformEventToDto(Event))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventDto updateEventStatus(Long EventId, String status, String managerEmail) {
        // Validate status
        if (!status.equals("ACTIVE") && !status.equals("CLOSED") && !status.equals("DRAFT")) {
            throw new RuntimeException("Invalid status. Must be ACTIVE, CLOSED, or DRAFT");
        }
        EventHubUser manager = userRepository.findEventHubUserByEmail(managerEmail)
                .orElseThrow(() -> new RuntimeException("manager not found"));

        if (manager.getOrganizer() == null) {
            throw new RuntimeException("manager does not have a Organizer assigned");
        }
        Event Event = manager.getOrganizer().getEvents().stream().filter(j -> j.getId().equals(EventId)).findFirst()
                .orElseThrow(() -> new RuntimeException("Event not found"));
        Event.setStatus(status);
        return ApplicationUtility.transformEventToDto(Event);
    }

    @Override
    @Transactional
    public EventDto createEvent(EventDto EventDto, String managerEmail) {
        // Validate manager and get their Organizer
        EventHubUser manager = userRepository.findEventHubUserByEmail(managerEmail)
                .orElseThrow(() -> new RuntimeException("manager not found"));
        if (manager.getOrganizer() == null) {
            throw new RuntimeException("manager does not have a Organizer assigned. Please contact admin.");
        }
        Event Event = tranformDtoToEntity(EventDto);
        Event.setPostedDate(Instant.now());
        Event.setApplicationsCount(0);
        Event.setStatus("DRAFT");
        Event.setOrganizer(manager.getOrganizer());
        Event savedEvent = EventRepository.save(Event);
        return ApplicationUtility.transformEventToDto(savedEvent);
    }

    @Override
    public List<EventApplicationDto> getApplicationsByEventForManager(Long EventId) {
        List<EventApplication> applications = EventApplicationRepository.findByEventIdOrderByAppliedAtAsc(EventId);
        return applications.stream()
                .map(EventApplication -> ApplicationUtility.mapToEventApplicationDto(EventApplication))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public boolean updateEventApplication(UpdateEventApplicationDto dto) {
        int updatedRows = EventApplicationRepository.updateStatusAndNotesById(
                dto.status().name(), dto.notes(),dto.applicationId(), ApplicationUtility.getLoggedInUser());
        return updatedRows > 0;
    }

    private Event tranformDtoToEntity(EventDto EventDto) {
        Event Event = new Event();
        BeanUtils.copyProperties(EventDto, Event);
        return Event;
    }

}
