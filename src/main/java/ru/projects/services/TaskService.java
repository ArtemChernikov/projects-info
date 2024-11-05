package ru.projects.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.projects.mapper.TaskMapper;
import ru.projects.model.Task;
import ru.projects.model.dto.TaskCreateDto;
import ru.projects.model.dto.TaskFullDto;
import ru.projects.model.dto.TaskViewDto;
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

    private void checkTaskExistsById(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new RuntimeException("Task Not Found.");
        }
    }
}
