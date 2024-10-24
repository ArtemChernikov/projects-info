package ru.projects.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.projects.model.Employee;
import ru.projects.model.Project;
import ru.projects.model.Specialization;
import ru.projects.model.Task;
import ru.projects.model.dto.EmployeeDto;
import ru.projects.model.dto.TaskCreateDto;
import ru.projects.model.enums.Priority;
import ru.projects.model.enums.TaskType;
import ru.projects.repository.TaskRepository;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 23.10.2024
 */
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public void save(TaskCreateDto taskCreateDto) {
        Task task = Task.builder()
                .name(taskCreateDto.getName())
                .description(taskCreateDto.getDescription())
                .project(Project.builder().projectId(taskCreateDto.getProject().getProjectId()).build())
                .developerId(taskCreateDto.getEmployee().getEmployeeId())
                .taskType(TaskType.fromDisplayName(taskCreateDto.getTaskType()))
                .priority(Priority.fromDisplayName(taskCreateDto.getPriority()))
                .build();
        taskRepository.save(task);
    }
}
