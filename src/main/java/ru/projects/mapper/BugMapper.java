package ru.projects.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.projects.model.Bug;
import ru.projects.model.dto.bug.BugCreateDto;
import ru.projects.model.dto.bug.BugUpdateDto;
import ru.projects.model.dto.bug.BugViewDto;
import ru.projects.model.enums.Priority;
import ru.projects.model.enums.Status;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProjectMapper.class},
        imports = {Priority.class, Status.class})
public interface BugMapper {

    @Mapping(target = "priority", expression = "java(Priority.fromDisplayName(bugCreateDto.getPriority()))")
    @Mapping(target = "status", expression = "java(Status.NEW)")
    Bug bugCreateDtoToBug(BugCreateDto bugCreateDto);

    @Mapping(target = "project", source = "project.name")
    @Mapping(target = "priority", expression = "java(bug.getPriority().getDisplayName())")
    @Mapping(target = "status", expression = "java(bug.getStatus().getDisplayName())")
    BugViewDto bugToBugViewDto(Bug bug);

    List<BugViewDto> bugsToBugViewDtos(List<Bug> bugs);

    @Mapping(target = "priority", expression = "java(bug.getPriority().getDisplayName())")
    BugUpdateDto bugToBugUpdateDto(Bug bug);
}
