package ru.projects.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.projects.mapper.ProjectMapper;
import ru.projects.model.Project;
import ru.projects.model.dto.project.ProjectCreateDto;
import ru.projects.model.dto.project.ProjectFullDto;
import ru.projects.model.dto.project.ProjectShortDto;
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
        Project oldProject = getProjectById(projectFullDto.getProjectId());
        Project project = projectMapper.projectFullDtoToProject(projectFullDto);
        project.setTasks(oldProject.getTasks());
        project.setBugs(oldProject.getBugs());
        return projectRepository.save(project);
    }

    public void deleteById(Long projectId) {
        getProjectById(projectId);
        projectRepository.deleteById(projectId);
    }

    public Page<ProjectFullDto> getAll(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(projectMapper::projectToProjectFullDto);
    }

    public Page<ProjectFullDto> getAllByEmployeeId(Pageable pageable, Long employeeId) {
        return projectRepository.findByEmployees_EmployeeId(pageable, employeeId)
                .map(projectMapper::projectToProjectFullDto);
    }

    public Set<ProjectShortDto> getAllProjectsShortDto() {
        return projectMapper.projectsToProjectsShortDto(projectRepository.findAll());
    }

    public Set<ProjectShortDto> getAllProjectShortDtoByEmployeeId(Long employeeId) {
        return projectMapper.projectsToProjectsShortDto(projectRepository.findByEmployees_EmployeeId(employeeId));
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project Not Found."));
    }

}
