package ru.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.projects.model.Employee;

import java.util.List;
import java.util.Set;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    List<Employee> findAllByRole_RoleName(String roleName);

    @Query("SELECT e FROM Employee e JOIN e.projects p WHERE p.projectId = :projectId" +
            " and e.specialization.specializationName IN :specializations")
    Set<Employee> findByProjectIdAndSpecialization(@Param("projectId") Long projectId,
                                                   @Param("specializations") List<String> specializations);
}
