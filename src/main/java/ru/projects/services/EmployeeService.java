package ru.projects.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.projects.model.Employee;
import ru.projects.model.Role;
import ru.projects.model.Specialization;
import ru.projects.model.dto.CreateEmployeeDto;
import ru.projects.model.dto.EmployeeDto;
import ru.projects.model.dto.EmployeeFullDto;
import ru.projects.repository.EmployeeRepository;
import ru.projects.repository.RoleRepository;
import ru.projects.repository.SpecializationRepository;

import java.util.Optional;

import static ru.projects.utils.Constants.AQA_ENGINEER_SPECIALIZATION_NAME;
import static ru.projects.utils.Constants.BACKEND_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.utils.Constants.DEVELOPER_ROLE_NAME;
import static ru.projects.utils.Constants.FRONTEND_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.utils.Constants.FULLSTACK_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.utils.Constants.PROJECT_MANAGER_ROLE_NAME;
import static ru.projects.utils.Constants.PROJECT_MANAGER_SPECIALIZATION_NAME;
import static ru.projects.utils.Constants.QA_ENGINEER_SPECIALIZATION_NAME;
import static ru.projects.utils.Constants.TESTER_ROLE_NAME;
import static ru.projects.utils.Constants.USER_ROLE_NAME;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 19.10.2024
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final SpecializationRepository specializationRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void save(CreateEmployeeDto createEmployeeDto) {
        //TODO ОБРАБОТАТЬ
        Specialization specialization = specializationRepository
                .findBySpecializationName(createEmployeeDto.getSpecializationName())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        Employee newEmployee = Employee.builder()
                .role(getRoleBySpecializationName(specialization.getSpecializationName()))
                .specialization(specialization)
                .firstName(createEmployeeDto.getFirstName())
                .lastName(createEmployeeDto.getLastName())
                .patronymicName(createEmployeeDto.getPatronymicName())
                .dateOfBirth(createEmployeeDto.getDateOfBirth())
                .phone(createEmployeeDto.getPhone())
                .email(createEmployeeDto.getEmail())
                .login(createEmployeeDto.getLogin())
                .password(passwordEncoder.encode(createEmployeeDto.getPassword()))
                .build();
        employeeRepository.save(newEmployee);
    }

    public Optional<EmployeeFullDto> getById(Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isEmpty()) {
            return Optional.empty();
        }
        Employee employee = optionalEmployee.get();
        EmployeeFullDto employeeFullDto = EmployeeFullDto.builder()
                .employeeId(employee.getEmployeeId())
                .specialization(employee.getSpecialization().getSpecializationName())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .patronymicName(employee.getPatronymicName())
                .dateOfBirth(employee.getDateOfBirth())
                .phone(employee.getPhone())
                .email(employee.getEmail())
                .login(employee.getLogin())
                .password(employee.getPassword())
                .build();
        return Optional.of(employeeFullDto);
    }

    public Employee update(EmployeeFullDto employeeFullDto) {
        Specialization specialization = specializationRepository
                .findBySpecializationName(employeeFullDto.getSpecialization())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        Employee employee = Employee.builder()
                .employeeId(employeeFullDto.getEmployeeId())
                .role(getRoleBySpecializationName(specialization.getSpecializationName()))
                .specialization(specialization)
                .role(getRoleBySpecializationName(specialization.getSpecializationName()))
                .firstName(employeeFullDto.getFirstName())
                .lastName(employeeFullDto.getLastName())
                .patronymicName(employeeFullDto.getPatronymicName())
                .dateOfBirth(employeeFullDto.getDateOfBirth())
                .phone(employeeFullDto.getPhone())
                .email(employeeFullDto.getEmail())
                .login(employeeFullDto.getLogin())
                .password(passwordEncoder.encode(employeeFullDto.getPassword()))
                .build();
        return employeeRepository.save(employee);
    }

    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
    }

    public Page<EmployeeFullDto> getAll(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(employee -> new EmployeeFullDto(employee.getEmployeeId(),
                        employee.getSpecialization().getSpecializationName(),
                        employee.getFirstName(), employee.getLastName(), employee.getPatronymicName(),
                        employee.getDateOfBirth(), employee.getPhone(), employee.getEmail(),
                        employee.getLogin(), employee.getPassword()));
    }

    public Page<EmployeeDto> getAllByFilter(Pageable pageable, Specification<Employee> filter) {
        return employeeRepository.findAll(filter, pageable)
                .map(employee -> new EmployeeDto(
                        employee.getSpecialization().getSpecializationName(),
                        employee.getFirstName(), employee.getLastName(), employee.getPatronymicName(),
                        employee.getDateOfBirth(), employee.getPhone(), employee.getEmail(),
                        employee.getLogin(), employee.getPassword()));
    }

    public int count() {
        return (int) employeeRepository.count();
    }

    private Role getRoleBySpecializationName(String specializationName) {
        return switch (specializationName) {
            case FULLSTACK_DEVELOPER_SPECIALIZATION_NAME, BACKEND_DEVELOPER_SPECIALIZATION_NAME,
                 FRONTEND_DEVELOPER_SPECIALIZATION_NAME -> roleRepository.findByRoleName(DEVELOPER_ROLE_NAME);
            case QA_ENGINEER_SPECIALIZATION_NAME, AQA_ENGINEER_SPECIALIZATION_NAME -> roleRepository
                    .findByRoleName(TESTER_ROLE_NAME);
            case PROJECT_MANAGER_SPECIALIZATION_NAME -> roleRepository.findByRoleName(PROJECT_MANAGER_ROLE_NAME);
            default -> roleRepository.findByRoleName(USER_ROLE_NAME);
        };
    }

}
