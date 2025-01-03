package ru.projects.view.bugs;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.projects.model.Employee;
import ru.projects.model.dto.bug.BugUpdateDto;
import ru.projects.model.dto.bug.BugViewDto;
import ru.projects.model.enums.Priority;
import ru.projects.service.BugService;
import ru.projects.service.EmployeeService;
import ru.projects.view.MainLayout;

import java.util.List;
import java.util.Optional;

@PageTitle("Bugs")
@Route(value = "qa-bugs/:bugID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed(value = {"ROLE_TEST"})
@Menu(order = 10, icon = "line-awesome/svg/bug-solid.svg")
@Slf4j
public class QABugsView extends Div implements BeforeEnterObserver {

    private static final String BUG_ID = "bugID";
    private static final String BUG_EDIT_ROUTE_TEMPLATE = "qa-bugs/%s/edit";

    private final Grid<BugViewDto> grid = new Grid<>(BugViewDto.class, false);

    private TextField name;
    private TextArea description;
    private ComboBox<String> priority;

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");

    private BeanValidationBinder<BugUpdateDto> binder;

    private BugUpdateDto bugUpdateDto;

    private Employee authenticatedEmployee;

    private final BugService bugService;

    public QABugsView(BugService bugService, EmployeeService employeeService) {
        this.bugService = bugService;
        authenticatedEmployee = employeeService.getCurrentEmployee();
        addClassNames("tasks-view");
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
        save.addClickListener(clickEvent -> updateBug());
        delete.addClickListener(clickEvent -> deleteBug());
    }

    private void updateBug() {
        log.info("VIEW: Updating bug.");
        try {
            if (this.bugUpdateDto == null) {
                this.bugUpdateDto = new BugUpdateDto();
            }
            binder.writeBean(this.bugUpdateDto);
            bugService.update(this.bugUpdateDto);
            clearForm();
            refreshGrid();
            log.info("VIEW: Bug update successfully.");
            Notification.show("The bug has been updated.", 3000, Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(QABugsView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            log.error("VIEW: Error updating the bug: {}", exception.getMessage());
            Notification.show(
                    "Error updating the bug. Somebody else has updated the record while you were making changes.",
                    3000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            log.error("VIEW: Error updating the bug: {}", validationException.getMessage());
            Notification.show("Failed to update the bug. Check again that all values are valid",
                    3000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteBug() {
        log.info("VIEW: Deleting bug.");
        try {
            if (this.bugUpdateDto == null) {
                this.bugUpdateDto = new BugUpdateDto();
            }
            binder.writeBean(this.bugUpdateDto);
            bugService.deleteById(this.bugUpdateDto.getBugId());
            clearForm();
            refreshGrid();
            log.info("VIEW: Bug delete successfully.");
            Notification.show("The bug has been removed.", 3000, Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(QABugsView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            log.error("VIEW: Error deleting the bug: {}", exception.getMessage());
            Notification.show(
                    "Error updating the data. Somebody else has updated the record while you were making changes.",
                    3000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            log.error("VIEW: Error deleting the bug: {}", validationException.getMessage());
            Notification.show("Failed to delete bug. Check again that all values are valid",
                    3000, Position.TOP_CENTER);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> bugId = event.getRouteParameters().get(BUG_ID).map(Long::parseLong);
        if (bugId.isPresent()) {
            Optional<BugUpdateDto> optionalBugFromBackend = bugService.getByIdForUpdate(bugId.get());
            if (optionalBugFromBackend.isPresent()) {
                BugUpdateDto bugFromBackend = optionalBugFromBackend.get();
                setPrioritiesToComboBox();
                fillEditForm(bugFromBackend);
            } else {
                Notification.show(String.format("The requested bug was not found, ID = %s", bugId.get()),
                        3000, Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(QABugsView.class);
            }
        }
    }

    private void configureValidationBinder() {
        binder = new BeanValidationBinder<>(BugUpdateDto.class);
        binder.forField(name)
                .asRequired("Bug name is required")
                .withValidator(value -> !value.trim().isEmpty(), "Bug name cannot be empty or spaces only")
                .bind(BugUpdateDto::getName, BugUpdateDto::setName);
        binder.forField(description)
                .asRequired("Description is required")
                .withValidator(value -> !value.trim().isEmpty(), "Description cannot be empty or spaces only")
                .bind(BugUpdateDto::getDescription, BugUpdateDto::setDescription);
        binder.forField(priority)
                .asRequired("Priority is required")
                .bind(BugUpdateDto::getPriority, BugUpdateDto::setPriority);
        binder.bindInstanceFields(this);
    }

    private void setRequiredFields() {
        name.setRequiredIndicatorVisible(true);
        description.setRequiredIndicatorVisible(true);
        priority.setRequiredIndicatorVisible(true);
    }

    private void configureGrid() {
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("project").setAutoWidth(true);
        grid.addColumn("priority").setAutoWidth(true);
        grid.addColumn("status").setAutoWidth(true);

        grid.addColumn(BugDescriptionDetails.createToggleDetailsRenderer(grid));
        grid.setDetailsVisibleOnClick(false);
        grid.setItemDetailsRenderer(BugDescriptionDetails.createBugDetailsRenderer());

        grid.setItems(query -> bugService.getAllByProjects(
                        PageRequest.of(query.getPage(), query.getPageSize(),
                                VaadinSpringDataHelpers.toSpringDataSort(query)), authenticatedEmployee.getProjects())
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(BUG_EDIT_ROUTE_TEMPLATE, event.getValue().getBugId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(QABugsView.class);
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
        name = new TextField("Task name");
        description = new TextArea("Description");
        priority = new ComboBox<>("Priority");

        priority.setWidth("min-content");

        setRequiredFields();
        formLayout.add(name, description, priority);

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
    }

    private void fillEditForm(BugUpdateDto value) {
        this.bugUpdateDto = value;
        binder.readBean(this.bugUpdateDto);
    }

    private void setPrioritiesToComboBox() {
        priority.setItems(List.of(Priority.HIGH.getDisplayName(), Priority.MEDIUM.getDisplayName(), Priority.LOW.getDisplayName()));
    }
}