package ru.projects.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.projects.mapper.ProjectMapper;
import ru.projects.model.Project;
import ru.projects.model.dto.ProjectCreateDto;
import ru.projects.model.dto.ProjectFullDto;
import ru.projects.model.dto.ProjectShortDto;
import ru.projects.repository.ProjectRepository;

import java.util.Optional;
import java.util.Set;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 19.10.2024
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public void save(ProjectCreateDto projectCreateDto) {
        Project newProject = projectMapper.projectCreateDtoToProject(projectCreateDto);
        projectRepository.save(newProject);
    }

    public Optional<ProjectFullDto> getById(Long id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isEmpty()) {
            return Optional.empty();
        }
        Project project = optionalProject.get();
        ProjectFullDto projectFullDto = projectMapper.projectToProjectFullDto(project);
        return Optional.of(projectFullDto);
    }

    public Project update(ProjectFullDto projectFullDto) {
        checkProjectExistsById(projectFullDto.getProjectId());
        Project project = projectMapper.projectFullDtoToProject(projectFullDto);
        return projectRepository.save(project);
    }

    public void deleteById(Long projectId) {
        checkProjectExistsById(projectId);
        projectRepository.deleteById(projectId);
    }

    public Page<ProjectFullDto> getAll(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(projectMapper::projectToProjectFullDto);
    }

    public Set<ProjectShortDto> getAllProjectsShortDto() {
        return projectMapper.projectsToProjectsShortDto(projectRepository.findAll());
    }

    private void checkProjectExistsById(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException("Project Not Found.");
        }
    }

}
