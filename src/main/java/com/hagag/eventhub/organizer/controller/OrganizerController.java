package com.hagag.eventhub.organizer.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import com.hagag.eventhub.dto.OrganizerDto;
import com.hagag.eventhub.entity.Organizer;
import com.hagag.eventhub.organizer.service.impl.OrganizerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organizer")
@RequiredArgsConstructor
public class OrganizerController {

    public final OrganizerService organizerService;

    @GetMapping(path = "/public")
    public ResponseEntity<List<OrganizerDto>> getAllOrganizers() {
        List<OrganizerDto> OrganizersDtoList = organizerService.getAllOrganizersPublic();
        return ResponseEntity.ok().body(OrganizersDtoList);
    }

    @GetMapping(path = "/admin")
    public ResponseEntity<List<Organizer>> getAllOrganizersAdmin() {
        List<Organizer> OrganizerDtoList = organizerService.getAllOrganizersAdmin();
        return ResponseEntity.ok().body(OrganizerDtoList);
    }

    @PostMapping(path = "/admin")
    public ResponseEntity<String> CreateOrganizer(@RequestBody @Valid OrganizerDto organizerDto) {
        boolean isExit = organizerService.CreateOrganizer(organizerDto);
        if (isExit) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Organizer created successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while creating Organizer");
        }
    }

    @DeleteMapping(path = "/{id}/admin")
    public ResponseEntity<String> deleteOrganizer(@PathVariable @NotBlank String id) {
        organizerService.deleteOrganizerById(Long.valueOf(id));
        return ResponseEntity.ok().body("Organizer deleted successfully");
    }

    @PutMapping(path = "/{id}/admin")
    public ResponseEntity<String> updateOrganizerDetails(@PathVariable @NotBlank String id, @RequestBody OrganizerDto OrganizerDto) {
        boolean isUpdate = organizerService.updateOrganizerDetails(Long.valueOf(id), OrganizerDto);
        if (isUpdate) {
            return ResponseEntity.status(HttpStatus.OK).body("Organizer updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while updating Organizer");
        }
    }
}
