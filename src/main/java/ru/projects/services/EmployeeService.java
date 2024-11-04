package ru.projects.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.projects.mapper.EmployeeMapper;
import ru.projects.model.Employee;
import ru.projects.model.dto.EmployeeDto;
import ru.projects.model.dto.EmployeeFullDto;
import ru.projects.model.dto.EmployeeShortDto;
import ru.projects.model.enums.TaskType;
import ru.projects.repository.EmployeeRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 19.10.2024
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final SpecializationService specializationService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeMapper employeeMapper;

    public void save(EmployeeDto employeeDto) {
        Employee employee = employeeMapper.employeeDtoToEmployee(employeeDto);
        employeeRepository.save(employee);
    }

    public Optional<EmployeeFullDto> getById(Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isEmpty()) {
            return Optional.empty();
        }
        Employee employee = optionalEmployee.get();
        EmployeeFullDto employeeFullDto = employeeMapper.employeeToEmployeeFullDto(employee);
        return Optional.of(employeeFullDto);
    }

    public Employee update(EmployeeFullDto employeeFullDto) {
        Employee oldEmployee = employeeRepository
                .findById(employeeFullDto.getEmployeeId()).orElseThrow(() -> new RuntimeException("Employee not found"));
        Employee employeeForUpdate = employeeMapper.employeeFullDtoToEmployee(employeeFullDto);
        employeeForUpdate.setPassword(getPasswordForUpdate(employeeFullDto.getPassword(), oldEmployee.getPassword()));
        return employeeRepository.save(employeeForUpdate);
    }

    public void deleteById(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }

    public Page<EmployeeFullDto> getAll(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(employeeMapper::employeeToEmployeeFullDto);
    }

    public Page<EmployeeDto> getAllByFilter(Pageable pageable, Specification<Employee> filter) {
        return employeeRepository.findAll(filter, pageable)
                .map(employeeMapper::employeeToEmployeeDto);
    }

    public Map<String, List<EmployeeShortDto>> getAllEmployeesBySpecialization() {
        List<Employee> employees = employeeRepository.findAll();
        return groupEmployeesBySpecializations(employees);
    }

    public Set<EmployeeShortDto> getAllEmployeesByProjectIdAndTaskType(Long projectId, String taskType) {
        TaskType enumTaskType = TaskType.fromDisplayName(taskType);
        List<String> specializationNames = specializationService.getEnumSpecializationsByTaskType(enumTaskType);
        return employeeRepository.findByProjectIdAndSpecialization(projectId, specializationNames).stream()
                .map(employeeMapper::employeeToEmployeeShortDto)
                .collect(Collectors.toSet());
    }

    public Map<String, List<EmployeeShortDto>> groupEmployeesBySpecializations(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(employee -> employee.getSpecialization().getSpecializationName(),
                        Collectors.mapping(employeeMapper::employeeToEmployeeShortDto, Collectors.toList())));
    }

    private String getPasswordForUpdate(String newPassword, String encodeOldPassword) {
        return newPassword.equals(encodeOldPassword) ? encodeOldPassword : passwordEncoder.encode(newPassword);
    }

}
