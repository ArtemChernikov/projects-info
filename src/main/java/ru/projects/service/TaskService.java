package ru.projects.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.projects.mapper.TaskMapper;
import ru.projects.model.Task;
import ru.projects.model.dto.task.TaskCreateDto;
import ru.projects.model.dto.task.TaskFullDto;
import ru.projects.model.dto.task.TaskViewDto;
import ru.projects.model.enums.Status;
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
    private final TaskMapper taskMapper;

    public void save(TaskCreateDto taskCreateDto) {
        Task task = taskMapper.taskCreateDtoToTask(taskCreateDto);
        taskRepository.save(task);
    }

    public Task update(TaskFullDto taskFullDto) {
        checkTaskExistsById(taskFullDto.getTaskId());
        Task task = taskMapper.taskFullDtoToTask(taskFullDto);
        return taskRepository.save(task);
    }

    public void deleteById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found");
        }
        taskRepository.deleteById(id);
    }

    public Optional<TaskFullDto> getById(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            return Optional.empty();
        }
        Task task = optionalTask.get();
        TaskFullDto taskFullDto = taskMapper.taskToTaskFullDto(task);
        return Optional.of(taskFullDto);
    }

    public Page<TaskViewDto> getAll(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(taskMapper::taskToTaskViewDto);
    }

    public Page<TaskViewDto> getAllByEmployeeId(Pageable pageable, Long employeeId) {
        return taskRepository.findAllByEmployee_EmployeeId(pageable, employeeId)
                .map(taskMapper::taskToTaskViewDto);
    }

    public void updateStatusById(Long taskId, String status) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        Status newStatus = Status.fromDisplayName(status);
        task.setStatus(newStatus);
        taskRepository.save(task);
    }

    private void checkTaskExistsById(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new RuntimeException("Task Not Found.");
        }
    }
}
