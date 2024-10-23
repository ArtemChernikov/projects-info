package ru.projects.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.projects.model.Employee;
import ru.projects.model.Project;
import ru.projects.model.dto.EmployeeShortDto;
import ru.projects.model.dto.ProjectCreateDto;
import ru.projects.model.dto.ProjectFullDto;
import ru.projects.model.enums.Status;
import ru.projects.repository.EmployeeRepository;
import ru.projects.repository.ProjectRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 19.10.2024
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final EmployeeService employeeService;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public void save(ProjectCreateDto projectCreateDto) {
        Set<EmployeeShortDto> employees = projectCreateDto.getEmployees();
        Set<Long> employeeIds = employees.stream()
                .map(EmployeeShortDto::getEmployeeId)
                .collect(Collectors.toSet());
        Set<Employee> employeesForSave = new HashSet<>(employeeRepository.findAllById(employeeIds));

        Project newProject = Project.builder()
                .name(projectCreateDto.getName())
                .startDate(projectCreateDto.getStartDate())
                .status(Status.NEW)
                .employees(employeesForSave)
                .build();
        employeesForSave.forEach(employee -> employee.getProjects().add(newProject));
        projectRepository.save(newProject);
    }

    public Optional<ProjectFullDto> getById(Long id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isEmpty()) {
            return Optional.empty();
        }
        Project project = optionalProject.get();
        ProjectFullDto projectFullDto = ProjectFullDto.builder()
                .projectId(project.getProjectId())
                .name(project.getName())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus().getDisplayName())
                .build();
        setEmployeesToProjectFullDto(project.getEmployees(), projectFullDto);
        return Optional.of(projectFullDto);
    }

    @Transactional
    public Project update(ProjectFullDto projectFullDto) {
        Project project = projectRepository.findById(projectFullDto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project Not Found."));
        Set<Employee> currentEmployees = project.getEmployees();
        Set<Long> newEmployeeIds = Stream.of(
                        projectFullDto.getProjectManagers(),
                        projectFullDto.getBackendDevelopers(),
                        projectFullDto.getFrontendDevelopers(),
                        projectFullDto.getFullstackDevelopers(),
                        projectFullDto.getQaEngineers(),
                        projectFullDto.getAqaEngineers(),
                        projectFullDto.getDevOps(),
                        projectFullDto.getDataScientists(),
                        projectFullDto.getDataAnalysts())
                .filter(Objects::nonNull) // Фильтруем null списки
                .flatMap(Collection::stream)
                .map(EmployeeShortDto::getEmployeeId)
                .collect(Collectors.toSet());

        currentEmployees.removeIf(employee -> !newEmployeeIds.contains(employee.getEmployeeId())
                && employee.getProjects().remove(project));

        Set<Employee> employeesForUpdate = employeeRepository.findAllById(newEmployeeIds).stream()
                .peek(employee -> employee.getProjects().add(project))
                .collect(Collectors.toSet());
        currentEmployees.addAll(employeesForUpdate);

        project.setName(projectFullDto.getName());
        project.setStartDate(projectFullDto.getStartDate());
        project.setEndDate(projectFullDto.getEndDate());
        project.setStatus(Status.fromDisplayName(projectFullDto.getStatus()));

        return projectRepository.save(project);
    }


    @Transactional
    public void deleteById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project Not Found."));

        for (Employee employee : project.getEmployees()) {
            employee.getProjects().remove(project);
        }
        projectRepository.deleteById(id);
    }

    public Page<ProjectFullDto> getAll(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(project -> {
                    ProjectFullDto projectFullDto = ProjectFullDto.builder()
                            .projectId(project.getProjectId())
                            .name(project.getName())
                            .startDate(project.getStartDate())
                            .endDate(project.getEndDate())
                            .status(project.getStatus().getDisplayName())
                            .build();

                    Set<Employee> employees = project.getEmployees();
                    setEmployeesToProjectFullDto(employees, projectFullDto);
                    return projectFullDto;
                });
    }

    private void setEmployeesToProjectFullDto(Set<Employee> employees, ProjectFullDto projectFullDto) {
        Map<String, List<EmployeeShortDto>> employeesBySpecializations = employeeService
                .groupEmployeesBySpecializations(new ArrayList<>(employees));

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

}
