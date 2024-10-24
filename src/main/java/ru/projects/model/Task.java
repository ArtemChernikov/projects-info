package ru.projects.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.projects.model.enums.Priority;
import ru.projects.model.enums.Status;
import ru.projects.model.enums.TaskType;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Builder
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long taskId;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private Long developerId;

    private String name;

    private String description;

    private TaskType taskType;

    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Status status;
}
