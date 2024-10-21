package ru.projects.views.projects;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.projects.model.dto.ProjectFullDto;
import ru.projects.model.enums.Status;
import ru.projects.services.ProjectService;
import ru.projects.services.SpecializationService;
import ru.projects.views.MainLayout;

import java.util.List;
import java.util.Optional;

@PageTitle("All Projects")
@Route(value = "projects/:projectID?/:action?(edit)", layout = MainLayout.class)
//@RolesAllowed("ADMIN")
public class ProjectsView extends Div implements BeforeEnterObserver {

    private static final String PROJECT_ID = "projectID";
    private static final String PROJECT_EDIT_ROUTE_TEMPLATE = "projects/%s/edit";

    private final Grid<ProjectFullDto> grid = new Grid<>(ProjectFullDto.class, false);

    private TextField name;
    private DatePicker startDate;
    private DatePicker endDate;
    private ComboBox<String> statusesComboBox;

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");

    private BeanValidationBinder<ProjectFullDto> binder;

    private ProjectFullDto projectFullDto;

    private final ProjectService projectService;

    public ProjectsView(ProjectService projectService) {
        this.projectService = projectService;
        addClassNames("projects-view");
        createUI();
    }

    private void createUI() {
        SplitLayout splitLayout = new SplitLayout();
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);
        add(splitLayout);

        configureGrid();
        configureValidationBinder();

        cancel.addClickListener(clickEvent -> {
            clearForm();
            refreshGrid();
        });
        save.addClickListener(clickEvent -> updateProject());
        delete.addClickListener(clickEvent -> deleteProject());
    }

    private void updateProject() {
        try {
            if (this.projectFullDto == null) {
                this.projectFullDto = new ProjectFullDto();
            }
            binder.writeBean(this.projectFullDto);
            projectService.update(this.projectFullDto);
            clearForm();
            refreshGrid();
            Notification.show("The project has been updated.", 3000, Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(ProjectsView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification.show(
                    "Error updating the project. Somebody else has updated the record while you were making changes.",
                    3000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("Failed to update the project. Check again that all values are valid",
                    3000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteProject() {
        try {
            if (this.projectFullDto == null) {
                this.projectFullDto = new ProjectFullDto();
            }
            binder.writeBean(this.projectFullDto);
            projectService.deleteById(this.projectFullDto.getProjectId());
            clearForm();
            refreshGrid();
            Notification.show("The project has been removed.", 3000, Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(ProjectsView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification.show(
                    "Error updating the project. Somebody else has updated the record while you were making changes.",
                    3000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("Failed to delete project. Check again that all values are valid",
                    3000, Position.TOP_CENTER);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> projectId = event.getRouteParameters().get(PROJECT_ID).map(Long::parseLong);
        if (projectId.isPresent()) {
            Optional<ProjectFullDto> projectFromBackend = projectService.getById(projectId.get());
            if (projectFromBackend.isPresent()) {
                fillEditForm(projectFromBackend.get());
            } else {
                Notification.show(String.format("The requested employee was not found, ID = %s", projectId.get()),
                        3000, Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(ProjectsView.class);
            }
        }
    }

    private void configureValidationBinder() {
        binder = new BeanValidationBinder<>(ProjectFullDto.class);
        binder.forField(name)
                .asRequired("Project Name is required")
                .bind(ProjectFullDto::getName, ProjectFullDto::setName);
        binder.forField(startDate)
                .asRequired("Start Date is required")
                .bind(ProjectFullDto::getStartDate, ProjectFullDto::setStartDate);
        binder.forField(endDate)
                .bind(ProjectFullDto::getEndDate, ProjectFullDto::setEndDate);
        binder.forField(statusesComboBox)
                .asRequired("Project Status is required")
                .bind(ProjectFullDto::getStatus, ProjectFullDto::setStatus);
        binder.bindInstanceFields(this);
    }

    private void setRequiredFiles() {
        name.setRequiredIndicatorVisible(true);
        startDate.setRequiredIndicatorVisible(true);
        statusesComboBox.setRequiredIndicatorVisible(true);
    }

    private void configureGrid() {
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("startDate").setAutoWidth(true);
        grid.addColumn("endDate").setAutoWidth(true);
        grid.addColumn("status").setAutoWidth(true);
        grid.setItems(query -> projectService.getAll(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(PROJECT_EDIT_ROUTE_TEMPLATE, event.getValue().getProjectId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ProjectsView.class);
            }
        });
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        name = new TextField("Project Name");
        startDate = new DatePicker("Start Date");
        endDate = new DatePicker("End Date");
        statusesComboBox = new ComboBox<>("Status");
        statusesComboBox.setWidth("min-content");
        setStatusesToComboBox();
        setRequiredFiles();
        formLayout.add(name, startDate, endDate, statusesComboBox);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        buttonLayout.add(save, delete, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        fillEditForm(null);
        statusesComboBox.clear();
    }

    private void fillEditForm(ProjectFullDto value) {
        this.projectFullDto = value;
        binder.readBean(this.projectFullDto);
    }

    private void setStatusesToComboBox() {
        statusesComboBox.setItems(List.of(Status.NEW.toString(), Status.IN_PROGRESS.toString(),
                Status.FINISHED.toString()));
    }
}