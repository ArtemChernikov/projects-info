package ru.projects.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.projects.model.Employee;
import ru.projects.model.dto.employee.EmployeeDto;
import ru.projects.model.dto.employee.EmployeeFullDto;
import ru.projects.model.dto.employee.EmployeeShortDto;
import ru.projects.service.RoleService;
import ru.projects.service.SpecializationService;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class EmployeeMapper {

    @Autowired
    protected RoleService roleService;

    @Autowired
    protected SpecializationService specializationService;

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

    public abstract List<EmployeeFullDto> employeesToEmployeesFullDto(List<Employee> employees);

    @Mapping(target = "role", expression = "java(roleService.getRoleBySpecializationName(employeeFullDto.getSpecialization()))")
    @Mapping(target = "specialization", expression = "java(specializationService.getSpecializationByName(employeeFullDto.getSpecialization()))")
    public abstract Employee employeeFullDtoToEmployee(EmployeeFullDto employeeFullDto);

    @Mapping(target = "name", expression = "java(getEmployeeFullName(employee))")
    public abstract EmployeeShortDto employeeToEmployeeShortDto(Employee employee);

    public abstract Employee employeeShortDtoToEmployeeWithOnlyId(EmployeeShortDto employeeShortDto);

    public abstract Set<Employee> employeesShortDtoToEmployeesWithOnlyId(Set<EmployeeShortDto> employeesShortDto);

    public String getEmployeeFullName(Employee employee) {
        return String.join(" ", employee.getLastName(), employee.getFirstName(), employee.getPatronymicName());
    }

}
