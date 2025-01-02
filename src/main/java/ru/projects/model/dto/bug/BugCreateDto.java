package ru.projects.model.dto.bug;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.projects.model.dto.project.ProjectShortDto;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 10.11.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BugCreateDto {

    @NotNull(message = "Project is required")
    private ProjectShortDto project;

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotEmpty(message = "Name cannot be empty")
    private String description;

    @NotEmpty(message = "Priority cannot be empty")
    private String priority;
}
