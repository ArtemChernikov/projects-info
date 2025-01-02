package ru.projects.model.dto.project;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.projects.model.dto.employee.EmployeeShortDto;

import java.time.LocalDate;
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
public class ProjectCreateDto {

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Start Date cannot be null")
    private LocalDate startDate;

    private Set<EmployeeShortDto> employees;

}
