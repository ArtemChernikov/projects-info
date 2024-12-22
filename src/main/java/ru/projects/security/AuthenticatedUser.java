package ru.projects.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.projects.model.User;
import ru.projects.repository.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    @Transactional
    public Optional<User> get() {
        Optional<UserDetails> authenticatedUser = authenticationContext.getAuthenticatedUser(UserDetails.class);
        if (authenticatedUser.isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByUsername(authenticatedUser.get().getUsername());
    }

    public void logout() {
        authenticationContext.logout();
    }

}
