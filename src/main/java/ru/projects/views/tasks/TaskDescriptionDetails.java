package ru.projects.views.tasks;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import ru.projects.model.dto.TaskViewDto;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 05.11.2024
 */
public class TaskDescriptionDetails {

    public static Renderer<TaskViewDto> createToggleDetailsRenderer(Grid<TaskViewDto> grid) {
        return LitRenderer.<TaskViewDto>of(
                        "<vaadin-button theme=\"tertiary\" @click=\"${handleClick}\">Description</vaadin-button>")
                .withFunction("handleClick",
                        task -> grid.setDetailsVisible(task,
                                !grid.isDetailsVisible(task)));
    }

    public static ComponentRenderer<TaskDetailsFormLayout, TaskViewDto> createTaskDetailsRenderer() {
        return new ComponentRenderer<>(TaskDetailsFormLayout::new, TaskDetailsFormLayout::setDescription);
    }

    private static class TaskDetailsFormLayout extends FormLayout {
        private final TextArea description = new TextArea("Description");

        public TaskDetailsFormLayout() {
            description.setReadOnly(true);
            add(description);
            setResponsiveSteps(new ResponsiveStep("0", 3));
            setColspan(description, 3);
        }

        public void setDescription(TaskViewDto taskViewDto) {
            description.setValue(taskViewDto.getDescription());
        }
    }
}
