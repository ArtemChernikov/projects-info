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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    private void setEmployeesToProjectFullDto(Set<Employee> employees, ProjectFullDto projectFullDto) {
        Map<String, List<EmployeeShortDto>> employeesBySpecializations = employeeService
                .groupEmployeesBySpecializations(new ArrayList<>(employees));
        if (employeesBySpecializations.containsKey(PROJECT_MANAGER_SPECIALIZATION_NAME)) {
            projectFullDto.setProjectManager(employeesBySpecializations.get(PROJECT_MANAGER_SPECIALIZATION_NAME).getFirst());
        }
        if (employeesBySpecializations.containsKey(BACKEND_DEVELOPER_SPECIALIZATION_NAME)) {
            projectFullDto.setBackendDevelopers(employeesBySpecializations.get(BACKEND_DEVELOPER_SPECIALIZATION_NAME));
        }
        if (employeesBySpecializations.containsKey(FRONTEND_DEVELOPER_SPECIALIZATION_NAME)) {
            projectFullDto.setFrontendDevelopers(employeesBySpecializations.get(FRONTEND_DEVELOPER_SPECIALIZATION_NAME));
        }
        if (employeesBySpecializations.containsKey(FULLSTACK_DEVELOPER_SPECIALIZATION_NAME)) {
            projectFullDto.setFullstackDeveloper(employeesBySpecializations.get(FULLSTACK_DEVELOPER_SPECIALIZATION_NAME).getFirst());
        }
        if (employeesBySpecializations.containsKey(QA_ENGINEER_SPECIALIZATION_NAME)) {
            projectFullDto.setQaEngineer(employeesBySpecializations.get(QA_ENGINEER_SPECIALIZATION_NAME).getFirst());
        }
        if (employeesBySpecializations.containsKey(AQA_ENGINEER_SPECIALIZATION_NAME)) {
            projectFullDto.setAqaEngineer(employeesBySpecializations.get(AQA_ENGINEER_SPECIALIZATION_NAME).getFirst());
        }
        if (employeesBySpecializations.containsKey(DEV_OPS_SPECIALIZATION_NAME)) {
            projectFullDto.setDevOps(employeesBySpecializations.get(DEV_OPS_SPECIALIZATION_NAME).getFirst());
        }
        if (employeesBySpecializations.containsKey(DATA_SCIENTIST_SPECIALIZATION_NAME)) {
            projectFullDto.setDataScientist(employeesBySpecializations.get(DATA_SCIENTIST_SPECIALIZATION_NAME).getFirst());
        }
        if (employeesBySpecializations.containsKey(DATA_ANALYST_SPECIALIZATION_NAME)) {
            projectFullDto.setDataAnalyst(employeesBySpecializations.get(DATA_ANALYST_SPECIALIZATION_NAME).getFirst());
        }
    }

    @Transactional
    public Project update(ProjectFullDto projectFullDto) {
        Project project = projectRepository.findById(projectFullDto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project Not Found."));

        Set<EmployeeShortDto> employees = new HashSet<>();
        if (projectFullDto.getProjectManager() != null) {
            employees.add(projectFullDto.getProjectManager());
        }
        if (projectFullDto.getBackendDevelopers() != null) {
            employees.addAll(projectFullDto.getBackendDevelopers());
        }
        if (projectFullDto.getFrontendDevelopers() != null) {
            employees.addAll(projectFullDto.getFrontendDevelopers());
        }
        if (projectFullDto.getFullstackDeveloper() != null) {
            employees.add(projectFullDto.getFullstackDeveloper());
        }
        if (projectFullDto.getQaEngineer() != null) {
            employees.add(projectFullDto.getQaEngineer());
        }
        if (projectFullDto.getAqaEngineer() != null) {
            employees.add(projectFullDto.getAqaEngineer());
        }
        if (projectFullDto.getDevOps() != null) {
            employees.add(projectFullDto.getDevOps());
        }
        if (projectFullDto.getDataScientist() != null) {
            employees.add(projectFullDto.getDataScientist());
        }
        if (projectFullDto.getDataAnalyst() != null) {
            employees.add(projectFullDto.getDataAnalyst());
        }
        Set<Long> employeeIds = employees.stream()
                .map(EmployeeShortDto::getEmployeeId)
                .collect(Collectors.toSet());
        Set<Employee> employeesForUpdate = new HashSet<>(employeeRepository.findAllById(employeeIds));
        employeesForUpdate.forEach(employee -> employee.getProjects().add(project));

        project.setName(projectFullDto.getName());
        project.setStartDate(projectFullDto.getStartDate());
        project.setEndDate(projectFullDto.getEndDate());
        project.setStatus(Status.fromDisplayName(projectFullDto.getStatus()));
        project.getEmployees();
        return projectRepository.save(project);
    }

    public void deleteById(Long id) {
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
//
//    public Page<EmployeeDto> getAllByFilter(Pageable pageable, Specification<Employee> filter) {
//        return employeeRepository.findAll(filter, pageable)
//                .map(employee -> new EmployeeDto(
//                        employee.getSpecialization().getSpecializationName(),
//                        employee.getFirstName(), employee.getLastName(), employee.getPatronymicName(),
//                        employee.getDateOfBirth(), employee.getPhone(), employee.getEmail(),
//                        employee.getLogin(), employee.getPassword()));
//    }
//
//    private Role getRoleBySpecializationName(String specializationName) {
//        return switch (specializationName) {
//            case FULLSTACK_DEVELOPER_SPECIALIZATION_NAME, BACKEND_DEVELOPER_SPECIALIZATION_NAME,
//                 FRONTEND_DEVELOPER_SPECIALIZATION_NAME -> roleRepository.findByRoleName(DEVELOPER_ROLE_NAME);
//            case QA_ENGINEER_SPECIALIZATION_NAME, AQA_ENGINEER_SPECIALIZATION_NAME -> roleRepository
//                    .findByRoleName(TESTER_ROLE_NAME);
//            case PROJECT_MANAGER_SPECIALIZATION_NAME -> roleRepository.findByRoleName(PROJECT_MANAGER_ROLE_NAME);
//            default -> roleRepository.findByRoleName(USER_ROLE_NAME);
//        };
//    }

}
