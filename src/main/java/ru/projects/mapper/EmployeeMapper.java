package ru.projects.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.projects.model.Employee;
import ru.projects.model.dto.EmployeeDto;
import ru.projects.model.dto.EmployeeFullDto;
import ru.projects.services.RoleService;
import ru.projects.services.SpecializationService;

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

    @Mapping(target = "specialization", expression = "java(employee.getSpecialization().getSpecializationName())")
    public abstract EmployeeFullDto employeeToEmployeeFullDto(Employee employee);

}
