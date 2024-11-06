package ru.projects.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.projects.model.Employee;
import ru.projects.repository.EmployeeRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByLogin(username);
        if (employee == null) {
            throw new UsernameNotFoundException("No employee present with login: " + username);
        }
        return new org.springframework.security.core.userdetails.User(employee.getLogin(), employee.getPassword(),
                getAuthorities(employee));
    }

    private static List<GrantedAuthority> getAuthorities(Employee employee) {
        return List.of(new SimpleGrantedAuthority(employee.getRole().getRoleName()));
    }

}
