package ru.projects.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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

    private EmployeeShortDto projectManager;

    private Set<EmployeeShortDto> backendDevelopers;

    private Set<EmployeeShortDto> frontendDevelopers;

    private EmployeeShortDto fullstackDeveloper;

    private EmployeeShortDto qaEngineer;

    private EmployeeShortDto aqaEngineer;

    private EmployeeShortDto devOps;

    private EmployeeShortDto dataScientist;

    private EmployeeShortDto dataAnalyst;

}
