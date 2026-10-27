package com.hagag.eventhub.repository;


import com.hagag.eventhub.entity.EventApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventApplicationRepository extends JpaRepository<EventApplication, Long> {

    boolean existsByUserIdAndEventId(Long userId, Long EventId);

    // Delete an application by user ID and Event ID
    void deleteByUserIdAndEventId(Long userId, Long EventId);

    // Find all applications by user ID
    List<EventApplication> findByUserIdOrderByAppliedAtDesc(Long userId);

    // Find applications by Event ID
    List<EventApplication> findByEventIdOrderByAppliedAtAsc(Long EventId);

    @Modifying
    int updateStatusAndNotesById(@Param("status") String status, @Param("notes") String notes,
            @Param("id") Long id, @Param("updatedBy") String updatedBy);
}