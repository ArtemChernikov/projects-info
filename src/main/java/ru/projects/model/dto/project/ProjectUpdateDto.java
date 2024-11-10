package ru.projects.model.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
public class ProjectUpdateDto {

    private Long projectId;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    private Set<Long> employeesIds = new HashSet<>();

}
