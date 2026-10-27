package com.hagag.eventhub.organizer.service.impl;

import lombok.RequiredArgsConstructor;
import com.hagag.eventhub.dto.OrganizerDto;
import com.hagag.eventhub.entity.Organizer;
import com.hagag.eventhub.organizer.service.IOrganizerService;
import com.hagag.eventhub.repository.OrganizerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizerService implements IOrganizerService {

    public final OrganizerRepository organizerRepository;

    @Override
    public List<OrganizerDto> getAllOrganizersPublic() {
        List<Organizer> OrganizerList = organizerRepository.findAll();
        return OrganizerList.stream().map(this::transformOrganizerToDto).collect(Collectors.toList());
    }

    @Override
    public List<Organizer> getAllOrganizersAdmin() {
        List<Organizer> OrganizerList = organizerRepository.findAll();
        return OrganizerList;
    }

    @Override
    public Boolean CreateOrganizer(OrganizerDto organizerDto) {
        Organizer organizer = transformOrganizerDtoToEntity(organizerDto);
        Organizer savedOrganizer = organizerRepository.save(organizer);
        return savedOrganizer.getId() != null && savedOrganizer.getId() > 0;
    }

    @Override
    public void deleteOrganizerById(Long id) {
        organizerRepository.deleteById(String.valueOf(id));
    }

    @Override
    @Transactional
    public Boolean updateOrganizerDetails(Long id, OrganizerDto OrganizerDto) {
        int organizer = organizerRepository.updateOrganizerDetails(id,OrganizerDto.name(),OrganizerDto.logo(),
                OrganizerDto.industry(),OrganizerDto.size(),OrganizerDto.rating(),
                OrganizerDto.locations(),OrganizerDto.founded(),OrganizerDto.description(),
                OrganizerDto.employees(),OrganizerDto.website());
        return organizer > 0;
    }

    private OrganizerDto transformOrganizerToDto(Organizer Organizer) {
//        List<JobDto> jobDtos = Organizer.getJobs().stream()
//                .map(job -> ApplicationUtility.transformJobToDto(job))
//                .collect(Collectors.toList());
        return new OrganizerDto(Organizer.getId(), Organizer.getName(), Organizer.getLogo(),
                Organizer.getIndustry(), Organizer.getSize(), Organizer.getRating(),
                Organizer.getLocations(), Organizer.getFounded(), Organizer.getDescription(),
                Organizer.getEmployees(), Organizer.getWebsite());
    }

    private Organizer transformOrganizerDtoToEntity(OrganizerDto OrganizerDto) {
        Organizer Organizer = new Organizer();
        BeanUtils.copyProperties(OrganizerDto, Organizer);
        return Organizer;
    }

}
