package com.hagag.eventhub.repository;


import com.hagag.eventhub.entity.EventHubUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventHubUserRepository extends JpaRepository<EventHubUser, Long> {

    Optional<EventHubUser> readUserByEmailOrMobileNumber(String email, String mobileNumber);

    Optional<EventHubUser> findEventHubUserByEmail(String email);

}