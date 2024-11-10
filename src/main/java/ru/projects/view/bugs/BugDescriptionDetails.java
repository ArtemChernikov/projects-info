package ru.projects.view.bugs;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import ru.projects.model.dto.bug.BugViewDto;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 10.11.2024
 */
public class BugDescriptionDetails {

    public static Renderer<BugViewDto> createToggleDetailsRenderer(Grid<BugViewDto> grid) {
        return LitRenderer.<BugViewDto>of(
                        "<vaadin-button theme=\"tertiary\" @click=\"${handleClick}\">Description</vaadin-button>")
                .withFunction("handleClick",
                        bug -> grid.setDetailsVisible(bug,
                                !grid.isDetailsVisible(bug)));
    }

    public static ComponentRenderer<BugDetailsFormLayout, BugViewDto> createBugDetailsRenderer() {
        return new ComponentRenderer<>(BugDetailsFormLayout::new, BugDetailsFormLayout::setDescription);
    }

    private static class BugDetailsFormLayout extends FormLayout {
        private final TextArea description = new TextArea("Description");

        public BugDetailsFormLayout() {
            description.setReadOnly(true);
            add(description);
            setResponsiveSteps(new ResponsiveStep("0", 3));
            setColspan(description, 3);
        }

        public void setDescription(BugViewDto bugViewDto) {
            description.setValue(bugViewDto.getDescription());
        }
    }
}
