package ru.projects.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.projects.mapper.BugMapper;
import ru.projects.model.Bug;
import ru.projects.model.Project;
import ru.projects.model.dto.bug.BugCreateDto;
import ru.projects.model.dto.bug.BugViewDto;
import ru.projects.model.enums.Status;
import ru.projects.repository.BugRepository;

import java.util.List;
import java.util.Set;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 10.11.2024
 */
@Service
@RequiredArgsConstructor
public class BugService {

    private final BugRepository bugRepository;
    private final BugMapper bugMapper;

    public void save(BugCreateDto bugCreateDto) {
        Bug bug = bugMapper.bugCreateDtoToBug(bugCreateDto);
        bugRepository.save(bug);
    }

    public Page<BugViewDto> getAllByProjects(Pageable pageable, Set<Project> projects) {
        List<Long> projectIds = projects.stream()
                .map(Project::getProjectId)
                .toList();
        return bugRepository.findAllByProject_ProjectIdIn(pageable, projectIds)
                .map(bugMapper::bugToBugViewDto);
    }

    public void updateStatusById(Long bugId, String status) {
        Bug bug = bugRepository.findById(bugId).orElseThrow(() -> new RuntimeException("Bug not found"));
        Status newStatus = Status.fromDisplayName(status);
        bug.setStatus(newStatus);
        bugRepository.save(bug);
    }

}
