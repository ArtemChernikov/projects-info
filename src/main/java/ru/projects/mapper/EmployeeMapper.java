package ru.projects.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.projects.model.Employee;
import ru.projects.model.dto.EmployeeDto;
import ru.projects.model.dto.EmployeeFullDto;
import ru.projects.model.dto.EmployeeShortDto;
import ru.projects.repository.EmployeeRepository;
import ru.projects.services.RoleService;
import ru.projects.services.SpecializationService;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class EmployeeMapper {

    @Autowired
    protected RoleService roleService;

    @Autowired
    protected SpecializationService specializationService;

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Mapping(target = "role", expression = "java(roleService.getRoleBySpecializationName(employeeDto.getSpecialization()))")
    @Mapping(target = "specialization", expression = "java(specializationService.getSpecializationByName(employeeDto.getSpecialization()))")
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(employeeDto.getPassword()))")
    public abstract Employee employeeDtoToEmployee(EmployeeDto employeeDto);

    @Mapping(target = "specialization", source = "specialization.specializationName")
    public abstract EmployeeDto employeeToEmployeeDto(Employee employee);

    @Mapping(target = "specialization", expression = "java(employee.getSpecialization().getSpecializationName())")
    public abstract EmployeeFullDto employeeToEmployeeFullDto(Employee employee);

    @Mapping(target = "name", expression = "java(getFullName(employee))")
    public abstract EmployeeShortDto employeeToEmployeeShortDto(Employee employee);

    public abstract Employee employeeShortDtoToEmployeeWithOnlyId(EmployeeShortDto employeeShortDto);

    @Named("employeesShortDtoToEmployees")
    public Set<Employee> employeesShortDtoToEmployees(Set<EmployeeShortDto> employeesShortDto) {
        Set<Long> employeeIds = employeesShortDto.stream()
                .map(EmployeeShortDto::getEmployeeId)
                .collect(Collectors.toSet());
        return new HashSet<>(employeeRepository.findAllById(employeeIds));
    }

    protected String getFullName(Employee employee) {
        return String.join(" ", employee.getLastName(), employee.getFirstName(), employee.getPatronymicName());
    }

}
