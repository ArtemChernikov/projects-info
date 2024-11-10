package ru.projects.model.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.projects.model.dto.employee.EmployeeShortDto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 21.10.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProjectFullDto {

    private Long projectId;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    private Set<EmployeeShortDto> projectManagers = new HashSet<>();

    private Set<EmployeeShortDto> backendDevelopers = new HashSet<>();

    private Set<EmployeeShortDto> frontendDevelopers = new HashSet<>();

    private Set<EmployeeShortDto> fullstackDevelopers = new HashSet<>();

    private Set<EmployeeShortDto> qaEngineers = new HashSet<>();

    private Set<EmployeeShortDto> aqaEngineers = new HashSet<>();

    private Set<EmployeeShortDto> devOps = new HashSet<>();

    private Set<EmployeeShortDto> dataScientists = new HashSet<>();

    private Set<EmployeeShortDto> dataAnalysts = new HashSet<>();

}
