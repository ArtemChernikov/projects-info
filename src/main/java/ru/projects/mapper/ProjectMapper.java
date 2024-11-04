package ru.projects.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import ru.projects.model.Project;
import ru.projects.model.dto.ProjectCreateDto;
import ru.projects.model.dto.ProjectFullDto;
import ru.projects.model.dto.ProjectShortDto;
import ru.projects.model.enums.Status;
import ru.projects.repository.EmployeeRepository;

@Mapper(componentModel = "spring", imports = {Status.class}, uses = {EmployeeMapper.class},
        builder = @Builder(disableBuilder = true))
public abstract class ProjectMapper {

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Mapping(target = "name", source = "name")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "status", expression = "java(Status.NEW)")
    @Mapping(target = "employees", source = "employees", qualifiedByName = "employeesShortDtoToEmployees")
    public abstract Project projectCreateDtoToProject(ProjectCreateDto projectCreateDto);

    @AfterMapping
    protected void establishBidirectionalRelation(ProjectCreateDto projectCreateDto, @MappingTarget Project project) {
        project.getEmployees().forEach(employee -> employee.getProjects().add(project));
    }

    @Mapping(target = "status", expression = "java(project.getStatus().getDisplayName())")
    public abstract ProjectFullDto projectToProjectFullDto(Project project);

    public abstract ProjectShortDto projectToProjectShortDto(Project project);

    public abstract Project projectShortDtoToProjectWithOnlyId(ProjectShortDto projectShortDto);

}
