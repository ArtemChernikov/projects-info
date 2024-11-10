package ru.projects.model.dto.bug;

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
public class BugViewDto {

    private Long bugId;

    private String project;

    private String name;

    private String description;

    private String priority;

    private String status;
}
