package ru.projects.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.projects.model.Task;
import ru.projects.model.dto.task.TaskCreateDto;
import ru.projects.model.dto.task.TaskFullDto;
import ru.projects.model.dto.task.TaskViewDto;
import ru.projects.model.enums.Priority;
import ru.projects.model.enums.Status;
import ru.projects.model.enums.TaskType;

import java.util.List;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class, ProjectMapper.class},
        imports = {TaskType.class, Priority.class, Status.class})
public abstract class TaskMapper {

    @Autowired
    protected EmployeeMapper employeeMapper;

    @Mapping(target = "taskType", expression = "java(TaskType.fromDisplayName(taskCreateDto.getTaskType()))")
    @Mapping(target = "priority", expression = "java(Priority.fromDisplayName(taskCreateDto.getPriority()))")
    @Mapping(target = "status", expression = "java(Status.NEW)")
    public abstract Task taskCreateDtoToTask(TaskCreateDto taskCreateDto);

    @Mapping(target = "taskType", expression = "java(task.getTaskType().getDisplayName())")
    @Mapping(target = "priority", expression = "java(task.getPriority().getDisplayName())")
    @Mapping(target = "status", expression = "java(task.getStatus().getDisplayName())")
    public abstract TaskFullDto taskToTaskFullDto(Task task);

    public abstract List<TaskFullDto> tasksToTaskFullDtos(List<Task> tasks);

    @Mapping(target = "taskType", expression = "java(TaskType.fromDisplayName(taskFullDto.getTaskType()))")
    @Mapping(target = "priority", expression = "java(Priority.fromDisplayName(taskFullDto.getPriority()))")
    @Mapping(target = "status", expression = "java(Status.fromDisplayName(taskFullDto.getStatus()))")
    public abstract Task taskFullDtoToTask(TaskFullDto taskFullDto);

    @Mapping(target = "project", source = "project.name")
    @Mapping(target = "employee", expression = "java(employeeMapper.getEmployeeFullName(task.getEmployee()))")
    @Mapping(target = "taskType", expression = "java(task.getTaskType().getDisplayName())")
    @Mapping(target = "priority", expression = "java(task.getPriority().getDisplayName())")
    @Mapping(target = "status", expression = "java(task.getStatus().getDisplayName())")
    public abstract TaskViewDto taskToTaskViewDto(Task task);
}
