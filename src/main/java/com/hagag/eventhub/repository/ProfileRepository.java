package com.hagag.eventhub.repository;

import com.hagag.eventhub.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}