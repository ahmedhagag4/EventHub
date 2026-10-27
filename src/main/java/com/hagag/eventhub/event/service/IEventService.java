package com.hagag.eventhub.event.service;

import com.hagag.eventhub.dto.EventApplicationDto;
import com.hagag.eventhub.dto.EventDto;
import com.hagag.eventhub.dto.UpdateEventApplicationDto;

import java.util.List;

public interface IEventService {

    /**
     * Get all Events posted by the manager's company
     * @param managerEmail the email of the manager
     * @return list of Events
     */
    List<EventDto> getManagerEvents(String managerEmail);

    /**
     * Update the status of a Event
     * @param EventId the ID of the Event
     * @param status the new status (ACTIVE, CLOSED, DRAFT)
     * @param managerEmail the email of the manager making the request
     * @return updated EventDto
     */
    EventDto updateEventStatus(Long EventId, String status, String managerEmail);

    /**
     * Create a new Event for the manager's company
     * @param EventDto the Event data
     * @param managerEmail the email of the manager creating the Event
     * @return created EventDto
     */
    EventDto createEvent(EventDto EventDto, String managerEmail);

    /**
     * Retrieves a list of Event applications submitted for a specific Event
     *
     * @param EventId the ID of the Event for which applications need to be retrieved
     * @return a list of Event applications submitted for the specified Event
     */
    List<EventApplicationDto> getApplicationsByEventForManager(Long EventId);

    /**
     * Updates an existing Event application with the provided details.
     *
     * @param updateEventApplicationDto the data transfer object containing
     *                                the details to update the Event application,
     *                                such as the application ID, status, and notes.
     * @return true if the Event application was successfully updated; false otherwise.
     */
    boolean updateEventApplication(UpdateEventApplicationDto updateEventApplicationDto);

}
