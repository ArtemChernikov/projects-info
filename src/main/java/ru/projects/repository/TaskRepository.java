package ru.projects.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.projects.model.Task;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByEmployee_EmployeeId(Pageable pageable, Long employeeId);

    List<Task> findAllByOrderByProject_Name();

    @Query("SELECT t FROM Task t WHERE t.project.projectId IN :projectIds")
    Page<Task> findAllByProjectIds(Pageable pageable, @Param("projectIds") List<Long> projectIds);

    @Query("SELECT t FROM Task t WHERE t.project.projectId IN :projectIds ORDER BY t.project.name")
    List<Task> findAllByProjectIdsOrderByProjectName(@Param("projectIds") List<Long> projectIds);

    @Query("SELECT t FROM Task t WHERE t.project.projectId IN :projectIds AND t.status IN ('NEW', 'IN_PROGRESS') ORDER BY t.project.name")
    List<Task> findAllActiveByProjectIdsOrderByProjectName(@Param("projectIds") List<Long> projectIds);

    @Query("SELECT t FROM Task t WHERE t.project.projectId IN :projectIds AND t.status = 'FINISHED' ORDER BY t.project.name")
    List<Task> findAllFinishedByProjectIdsOrderByProjectName(@Param("projectIds") List<Long> projectIds);
}
