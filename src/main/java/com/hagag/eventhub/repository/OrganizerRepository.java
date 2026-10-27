package com.hagag.eventhub.repository;

import com.hagag.eventhub.entity.Organizer;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, String> {

    @CacheEvict(value = "organizer", allEntries = true)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int updateOrganizerDetails(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("logo") String logo,
            @Param("industry") String industry,
            @Param("size") String size,
            @Param("rating") BigDecimal rating,
            @Param("locations") String locations,
            @Param("founded") Integer founded,
            @Param("description") String description,
            @Param("employees") Integer employees,
            @Param("website") String website
    );

}
