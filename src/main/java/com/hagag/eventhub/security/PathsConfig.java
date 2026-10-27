package com.hagag.eventhub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PathsConfig {

    @Bean(name = "publicPaths")
    public List<String> publicPaths() {
        return List.of(
                "/api/contacts/public",
                "/api/auth/login/public",
                "/api/organizer/public",
                "/api/auth/register/public",
                "/api/csrf-token/public",
                "/api/logging/public",
                "/api/swagger-ui.html",
                "/swagger-ui/**",
                "/api/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**",
                "/eventportal/actuator/**",
                "/api/todos/**",
                "/api/posts/**"
        );
    }

    @Bean(name = "securedPaths")
    public List<String> securedPaths() {
        return List.of(
                "/api/**"
        );
    }

    @Bean(name = "attendeePaths")
    public List<String> attendeePaths() {
        return List.of(
                "/api/users/profile/attendee",
                "/api/users/profile/picture/attendee",
                "/api/users/profile/resume/attendee",
                "/api/users/saved-events/${eventId}/attendee",
                "/api/users/saved-events/attendee",
                "/api/users/event-applications/attendee",
                "/api/users/event-applications/${eventId}/attendee"
        );
    }

    @Bean(name = "managerPaths")
    public List<String> managerPaths() {
        return List.of(
                "/api/events/manager",
                "/api/events/${eventId}/status/manager",
                "/api/events/applications/${eventId}/manager",
                "/api/events/applications/manager"
        );
    }

    @Bean(name = "adminPaths")
    public List<String> adminPaths() {
        return List.of(
                "/api/contacts/admin",
                "/api/contacts/sort/admin",
                "/api/contacts/page/admin",
                "/api/contacts/${id}/status/admin",
                "/api/organizer/admin",
                "/api/organizer/${id}/admin",
                "/api/users/search/admin",
                "/api/users/${userId}/role/manager/admin",
                "/api/users/${userId}/role/manager/admin"
        );
    }

}
