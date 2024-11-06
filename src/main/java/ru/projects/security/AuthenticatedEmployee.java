package ru.projects.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.projects.model.Employee;
import ru.projects.repository.EmployeeRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AuthenticatedEmployee {

    private final EmployeeRepository employeeRepository;
    private final AuthenticationContext authenticationContext;

    @Transactional
    public Optional<Employee> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> employeeRepository.findByLogin(userDetails.getUsername()));
    }

    public void logout() {
        authenticationContext.logout();
    }

}
