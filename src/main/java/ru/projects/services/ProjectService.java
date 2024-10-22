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

    @Transactional
    public Project update(ProjectFullDto projectFullDto) {
        Project project = projectRepository.findById(projectFullDto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project Not Found."));

        // Собираем новых сотрудников из DTO
        Set<EmployeeShortDto> newEmployees = new HashSet<>();
        if (projectFullDto.getProjectManager() != null) {
            newEmployees.add(projectFullDto.getProjectManager());
        }
        if (projectFullDto.getBackendDevelopers() != null) {
            newEmployees.addAll(projectFullDto.getBackendDevelopers());
        }
        if (projectFullDto.getFrontendDevelopers() != null) {
            newEmployees.addAll(projectFullDto.getFrontendDevelopers());
        }
        if (projectFullDto.getFullstackDeveloper() != null) {
            newEmployees.add(projectFullDto.getFullstackDeveloper());
        }
        if (projectFullDto.getQaEngineer() != null) {
            newEmployees.add(projectFullDto.getQaEngineer());
        }
        if (projectFullDto.getAqaEngineer() != null) {
            newEmployees.add(projectFullDto.getAqaEngineer());
        }
        if (projectFullDto.getDevOps() != null) {
            newEmployees.add(projectFullDto.getDevOps());
        }
        if (projectFullDto.getDataScientist() != null) {
            newEmployees.add(projectFullDto.getDataScientist());
        }
        if (projectFullDto.getDataAnalyst() != null) {
            newEmployees.add(projectFullDto.getDataAnalyst());
        }

        Set<Employee> currentEmployees = project.getEmployees();

        if (newEmployees.isEmpty()) {
            currentEmployees.forEach(employee -> employee.getProjects().remove(project));
            currentEmployees.clear();
            project.setEmployees(currentEmployees);
        } else {
            // Идентификаторы новых сотрудников
            Set<Long> newEmployeeIds = newEmployees.stream()
                    .map(EmployeeShortDto::getEmployeeId)
                    .collect(Collectors.toSet());

            // Удаляем сотрудников, которые больше не должны быть связаны с проектом
            currentEmployees.removeIf(employee -> !newEmployeeIds.contains(employee.getEmployeeId()));

            // Загружаем новых сотрудников по их ID
            Set<Employee> employeesForUpdate = new HashSet<>(employeeRepository.findAllById(newEmployeeIds));
            employeesForUpdate.forEach(employee -> {
                if (!currentEmployees.contains(employee)) {
                    employee.getProjects().add(project); // Добавляем проект к новому сотруднику
                }
            });

            // Объединяем текущих сотрудников с новыми
            currentEmployees.addAll(employeesForUpdate);

        }

        project.setEmployees(currentEmployees);

        // Обновляем проект
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
            employee.getProjects().remove(project);  // Удаляем проект из сотрудников
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
        if (employeesBySpecializations.containsKey(PROJECT_MANAGER_SPECIALIZATION_NAME)) {
            projectFullDto.setProjectManager(employeesBySpecializations.get(PROJECT_MANAGER_SPECIALIZATION_NAME).getFirst());
        }
        if (employeesBySpecializations.containsKey(BACKEND_DEVELOPER_SPECIALIZATION_NAME)) {
            projectFullDto.setBackendDevelopers(new HashSet<>(employeesBySpecializations.get(BACKEND_DEVELOPER_SPECIALIZATION_NAME)));
        }
        if (employeesBySpecializations.containsKey(FRONTEND_DEVELOPER_SPECIALIZATION_NAME)) {
            projectFullDto.setFrontendDevelopers(new HashSet<>(employeesBySpecializations.get(FRONTEND_DEVELOPER_SPECIALIZATION_NAME)));
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

}
