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
import ru.projects.services.EmployeeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.projects.utils.Constants.AQA_ENGINEER_SPECIALIZATION_NAME;
import static ru.projects.utils.Constants.BACKEND_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.utils.Constants.DATA_ANALYST_SPECIALIZATION_NAME;
import static ru.projects.utils.Constants.DATA_SCIENTIST_SPECIALIZATION_NAME;
import static ru.projects.utils.Constants.DEV_OPS_SPECIALIZATION_NAME;
import static ru.projects.utils.Constants.FRONTEND_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.utils.Constants.FULLSTACK_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.utils.Constants.PROJECT_MANAGER_SPECIALIZATION_NAME;
import static ru.projects.utils.Constants.QA_ENGINEER_SPECIALIZATION_NAME;

@Mapper(componentModel = "spring", imports = {Status.class}, uses = {EmployeeMapper.class},
        builder = @Builder(disableBuilder = true))
public abstract class ProjectMapper {

    @Autowired
    protected EmployeeService employeeService;

    @Autowired
    protected EmployeeMapper employeeMapper;

    @Mapping(target = "name", source = "name")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "status", expression = "java(Status.NEW)")
    @Mapping(target = "employees", source = "employees")
    public abstract Project projectCreateDtoToProject(ProjectCreateDto projectCreateDto);

    @Mapping(target = "status", expression = "java(project.getStatus().getDisplayName())")
    public abstract ProjectFullDto projectToProjectFullDto(Project project);

    @AfterMapping
    protected void setEmployeesToProjectFullDto(Project project, @MappingTarget ProjectFullDto projectFullDto) {
        Map<String, List<EmployeeShortDto>> employeesBySpecializations = employeeService
                .groupEmployeesBySpecializations(new ArrayList<>(project.getEmployees()));

        Map<String, Consumer<Set<EmployeeShortDto>>> specializationSetters = Map.of(
                PROJECT_MANAGER_SPECIALIZATION_NAME, projectFullDto::setProjectManagers,
                BACKEND_DEVELOPER_SPECIALIZATION_NAME, projectFullDto::setBackendDevelopers,
                FRONTEND_DEVELOPER_SPECIALIZATION_NAME, projectFullDto::setFrontendDevelopers,
                FULLSTACK_DEVELOPER_SPECIALIZATION_NAME, projectFullDto::setFullstackDevelopers,
                QA_ENGINEER_SPECIALIZATION_NAME, projectFullDto::setQaEngineers,
                AQA_ENGINEER_SPECIALIZATION_NAME, projectFullDto::setAqaEngineers,
                DEV_OPS_SPECIALIZATION_NAME, projectFullDto::setDevOps,
                DATA_SCIENTIST_SPECIALIZATION_NAME, projectFullDto::setDataScientists,
                DATA_ANALYST_SPECIALIZATION_NAME, projectFullDto::setDataAnalysts
        );

        specializationSetters.forEach((specialization, setter) -> {
            List<EmployeeShortDto> employeeShortDtos = employeesBySpecializations.get(specialization);
            if (employeeShortDtos != null) {
                setter.accept(new HashSet<>(employeeShortDtos));
            }
        });
    }

    @Mapping(target = "status", expression = "java(Status.fromDisplayName(projectFullDto.getStatus()))")
    public abstract Project projectFullDtoToProject(ProjectFullDto projectFullDto);

    @AfterMapping
    protected void setEmployeesFromProjectFullDto(ProjectFullDto projectFullDto, @MappingTarget Project project) {
        Set<Employee> employees = Stream.of(
                        projectFullDto.getProjectManagers(),
                        projectFullDto.getBackendDevelopers(),
                        projectFullDto.getFrontendDevelopers(),
                        projectFullDto.getFullstackDevelopers(),
                        projectFullDto.getQaEngineers(),
                        projectFullDto.getAqaEngineers(),
                        projectFullDto.getDevOps(),
                        projectFullDto.getDataScientists(),
                        projectFullDto.getDataAnalysts())
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(employeeShortDto -> employeeMapper.employeeShortDtoToEmployeeWithOnlyId(employeeShortDto))
                .collect(Collectors.toSet());
        project.setEmployees(employees);
    }

    public abstract ProjectShortDto projectToProjectShortDto(Project project);

    public abstract Set<ProjectShortDto> projectsToProjectsShortDto(List<Project> projects);

    public abstract Project projectShortDtoToProjectWithOnlyId(ProjectShortDto projectShortDto);

}
