package ru.projects.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 31.10.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class TaskViewDto {

    private Long taskId;

    private String name;

    private String description;

    private String project;

    private String employee;

    private String taskType;

    private String priority;

    private String status;

}
