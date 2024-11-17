package ru.projects.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.projects.model.Task;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByTaskId(Long id);

    Page<Task> findAllByEmployee_EmployeeId(Pageable pageable, Long employeeId);

    List<Task> findAllByOrderByProject_NameAsc();
}
