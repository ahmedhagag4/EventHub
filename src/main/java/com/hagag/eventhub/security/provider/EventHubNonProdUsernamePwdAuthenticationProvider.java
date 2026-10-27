package com.hagag.eventhub.security.provider;


import com.hagag.eventhub.entity.EventHubUser;
import com.hagag.eventhub.repository.EventHubUserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("!prod")
@Component
@RequiredArgsConstructor
public class EventHubNonProdUsernamePwdAuthenticationProvider implements AuthenticationProvider {

    private final EventHubUserRepository eventHubUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();
        EventHubUser eventHubUser = eventHubUserRepository.findEventHubUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User details not found for the user: " + username)
        );
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(eventHubUser.getRole().getName()));
            return new UsernamePasswordAuthenticationToken(eventHubUser, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
