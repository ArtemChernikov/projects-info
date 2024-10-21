package ru.projects.services;

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
import ru.projects.repository.ProjectRepository;

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
public class ProjectService {

    private final ProjectRepository projectRepository;

    public void save(ProjectCreateDto projectCreateDto) {
        Set<EmployeeShortDto> employees = projectCreateDto.getEmployees();
        Set<Employee> employeesForSave = employees.stream()
                .map(employeeShortDto -> Employee.builder().employeeId(employeeShortDto.getEmployeeId()).build()).collect(Collectors.toSet());
        Project newProject = Project.builder()
                .name(projectCreateDto.getName())
                .startDate(projectCreateDto.getStartDate())
                .status(Status.NEW)
                .employees(employeesForSave)
                .build();
        projectRepository.save(newProject);
    }

    public Optional<ProjectFullDto> getById(Long id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isEmpty()) {
            return Optional.empty();
        }
        Project project = optionalProject.get();
        Set<Employee> employees = project.getEmployees();
        ProjectFullDto projectFullDto = ProjectFullDto.builder()
                .projectId(project.getProjectId())
                .name(project.getName())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus().toString())
                .employees(employeesToEmployeeShortDtos(employees))
                .build();
        return Optional.of(projectFullDto);
    }

    private Set<EmployeeShortDto> employeesToEmployeeShortDtos(Set<Employee> employees) {
        StringBuilder stringBuilder = new StringBuilder();
        return employees.stream().map(employee -> {
            stringBuilder.append(employee.getFirstName());
            stringBuilder.append(" ");
            stringBuilder.append(employee.getLastName());
            stringBuilder.append(" ");
            stringBuilder.append(employee.getPatronymicName());
            stringBuilder.append(" ");
            EmployeeShortDto employeeShortDto = new EmployeeShortDto(employee.getEmployeeId(), stringBuilder.toString());
            stringBuilder.setLength(0);
            return employeeShortDto;
        }).collect(Collectors.toSet());
    }

    public Project update(ProjectFullDto projectFullDto) {
        Project project = projectRepository.findById(projectFullDto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project Not Found."));
        project.setName(projectFullDto.getName());
        project.setStartDate(projectFullDto.getStartDate());
        project.setEndDate(projectFullDto.getEndDate());
        project.setStatus(Status.valueOf(projectFullDto.getStatus()));

        return projectRepository.save(project);
    }

    public void deleteById(Long id) {
        projectRepository.deleteById(id);
    }

    //
    public Page<ProjectFullDto> getAll(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(project -> new ProjectFullDto(project.getProjectId(), project.getName(),
                        project.getStartDate(), project.getEndDate(), project.getStatus().toString(),
                        employeesToEmployeeShortDtos(project.getEmployees())));
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
