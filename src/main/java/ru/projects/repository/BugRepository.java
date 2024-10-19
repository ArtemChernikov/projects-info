package ru.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.projects.model.Bug;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {
}
