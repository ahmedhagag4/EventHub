package com.hagag.eventhub.security.provider;


import lombok.RequiredArgsConstructor;
import com.hagag.eventhub.entity.EventHubUser;
import com.hagag.eventhub.repository.EventHubUserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("prod")
@Component
@RequiredArgsConstructor
public class EventHubUsernamePwdAuthenticationProvider implements AuthenticationProvider {

    private final EventHubUserRepository EventHubUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();
        EventHubUser EventHubUser = EventHubUserRepository.findEventHubUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User details not found for the user: " + username)
        );
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(EventHubUser.getRole().getName()));
        if (passwordEncoder.matches(pwd, EventHubUser.getPasswordHash())) {
            return new UsernamePasswordAuthenticationToken(EventHubUser, null, authorities);
        } else {
            throw new BadCredentialsException("Invalid password!");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
