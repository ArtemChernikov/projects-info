package ru.projects.model.dto.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.projects.model.dto.employee.EmployeeShortDto;
import ru.projects.model.dto.project.ProjectShortDto;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 19.10.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TaskCreateDto {

    private String name;

    private String description;

    private ProjectShortDto project;

    private EmployeeShortDto employee;

    private String taskType;

    private String priority;

}
