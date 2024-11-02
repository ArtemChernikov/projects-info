package ru.projects.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.projects.mapper.EmployeeMapper;
import ru.projects.model.Employee;
import ru.projects.model.Project;
import ru.projects.model.Task;
import ru.projects.model.dto.ProjectShortDto;
import ru.projects.model.dto.TaskCreateDto;
import ru.projects.model.dto.TaskFullDto;
import ru.projects.model.enums.Priority;
import ru.projects.model.enums.Status;
import ru.projects.model.enums.TaskType;
import ru.projects.repository.TaskRepository;

import java.util.Optional;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 23.10.2024
 */
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    public void save(TaskCreateDto taskCreateDto) {
        Task task = Task.builder()
                .name(taskCreateDto.getName())
                .description(taskCreateDto.getDescription())
                .project(Project.builder().projectId(taskCreateDto.getProject().getProjectId()).build())
                .employee(Employee.builder().employeeId(taskCreateDto.getEmployee().getEmployeeId()).build())
                .taskType(TaskType.fromDisplayName(taskCreateDto.getTaskType()))
                .priority(Priority.fromDisplayName(taskCreateDto.getPriority()))
                .status(Status.NEW)
                .build();
        taskRepository.save(task);
    }

    public Optional<TaskFullDto> getById(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            return Optional.empty();
        }
        Task task = optionalTask.get();
        TaskFullDto taskFullDto = TaskFullDto.builder()
                .taskId(task.getTaskId())
                .name(task.getName())
                .description(task.getDescription())
                .project(new ProjectShortDto(task.getProject().getProjectId(), task.getProject().getName()))
                .employee(employeeMapper.employeeToEmployeeShortDto(task.getEmployee()))
                .taskType(task.getTaskType().getDisplayName())
                .priority(task.getPriority().getDisplayName())
                .status(task.getStatus().getDisplayName())
                .build();
        return Optional.of(taskFullDto);
    }

    public Page<TaskFullDto> getAll(Pageable pageable) {
        StringBuilder stringBuilder = new StringBuilder();
        return taskRepository.findAll(pageable)
                .map(task -> {
                    Employee employee = task.getEmployee();
                    Project project = task.getProject();
                    return new TaskFullDto(task.getTaskId(),
                            task.getName(), task.getDescription(),
                            new ProjectShortDto(project.getProjectId(), project.getName()),
                            employeeMapper.employeeToEmployeeShortDto(employee),
                            task.getTaskType().getDisplayName(), task.getPriority().getDisplayName(),
                            task.getStatus().getDisplayName());
                });
    }
}
