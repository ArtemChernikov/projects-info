package ru.projects.mapper;

import org.mapstruct.Mapper;
import ru.projects.model.Project;
import ru.projects.model.dto.ProjectShortDto;

@Mapper(componentModel = "spring")
public interface ProjectMapper {


    ProjectShortDto projectToProjectShortDto(Project project);

    Project projectShortDtoToProject(ProjectShortDto projectShortDto);

}
