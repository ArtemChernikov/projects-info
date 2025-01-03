package ru.projects.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.projects.model.Bug;

import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {

    Page<Bug> findAllByProject_ProjectIdIn(Pageable pageable, List<Long> projectIds);

    @Query("SELECT b FROM Bug b WHERE b.project.projectId IN :projectIds ORDER BY b.project.name")
    List<Bug> findAllByProjectIdsOrderByProjectName(List<Long> projectIds);

}
