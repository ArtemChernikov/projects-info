package ru.projects.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.projects.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByProjectId(Long id);

    Page<Project> findByEmployees_EmployeeId(Pageable pageable, Long employeeId);

}
