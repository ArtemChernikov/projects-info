package ru.projects.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.projects.model.Bug;

import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {

    Page<Bug> findAllByProject_ProjectIdIn(Pageable pageable, List<Long> projectIds);

}
