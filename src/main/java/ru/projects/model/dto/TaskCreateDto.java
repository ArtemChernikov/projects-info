package ru.projects.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

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
