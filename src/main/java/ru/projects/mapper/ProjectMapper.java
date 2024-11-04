package ru.projects.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import ru.projects.model.Employee;
import ru.projects.model.Project;
import ru.projects.model.dto.EmployeeShortDto;
import ru.projects.model.dto.ProjectCreateDto;
import ru.projects.model.dto.ProjectFullDto;
import ru.projects.model.dto.ProjectShortDto;
import ru.projects.model.enums.Status;
import ru.projects.repository.EmployeeRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {Status.class}, builder = @Builder(disableBuilder = true))
public abstract class ProjectMapper {

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Mapping(target = "name", source = "name")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "status", expression = "java(Status.NEW)")
    @Mapping(target = "employees", expression = "java(getEmployeesFromProjectCreateDto(projectCreateDto))")
    public abstract Project projectCreateDtoToProject(ProjectCreateDto projectCreateDto);

    public abstract ProjectFullDto projectToProjectFullDto(Project project);

    public abstract ProjectShortDto projectToProjectShortDto(Project project);

    public abstract Project projectShortDtoToProject(ProjectShortDto projectShortDto);

    @AfterMapping
    protected void establishBidirectionalRelation(ProjectCreateDto projectCreateDto, @MappingTarget Project project) {
        project.getEmployees().forEach(employee -> employee.getProjects().add(project));
    }

    protected Set<Employee> getEmployeesFromProjectCreateDto(ProjectCreateDto projectCreateDto) {
        Set<EmployeeShortDto> employees = projectCreateDto.getEmployees();
        Set<Long> employeeIds = employees.stream()
                .map(EmployeeShortDto::getEmployeeId)
                .collect(Collectors.toSet());
        return new HashSet<>(employeeRepository.findAllById(employeeIds));
    }

}
