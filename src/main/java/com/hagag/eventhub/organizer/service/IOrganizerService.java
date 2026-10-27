package com.hagag.eventhub.organizer.service;

import com.hagag.eventhub.dto.OrganizerDto;
import com.hagag.eventhub.entity.Organizer;

import java.util.List;

public interface IOrganizerService {

    List<OrganizerDto> getAllOrganizersPublic();
    List<Organizer> getAllOrganizersAdmin();

    Boolean CreateOrganizer(OrganizerDto OrganizerDto);
    void deleteOrganizerById(Long id);

    Boolean updateOrganizerDetails(Long id, OrganizerDto OrganizerDto);
}
