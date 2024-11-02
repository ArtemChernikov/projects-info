package ru.projects.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.projects.model.Task;
import ru.projects.model.dto.TaskCreateDto;
import ru.projects.model.dto.TaskFullDto;
import ru.projects.model.enums.Priority;
import ru.projects.model.enums.Status;
import ru.projects.model.enums.TaskType;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class, ProjectMapper.class},
        imports = {TaskType.class, Priority.class, Status.class})
public interface TaskMapper {

    @Mapping(target = "taskType", expression = "java(TaskType.fromDisplayName(taskCreateDto.getTaskType()))")
    @Mapping(target = "priority", expression = "java(Priority.fromDisplayName(taskCreateDto.getPriority()))")
    @Mapping(target = "status", expression = "java(Status.NEW)")
    Task taskCreateDtoToTask(TaskCreateDto taskCreateDto);

    @Mapping(target = "taskType", expression = "java(task.getTaskType().getDisplayName())")
    @Mapping(target = "priority", expression = "java(task.getPriority().getDisplayName())")
    @Mapping(target = "status", expression = "java(task.getStatus().getDisplayName())")
    TaskFullDto taskToTaskFullDto(Task task);
}
