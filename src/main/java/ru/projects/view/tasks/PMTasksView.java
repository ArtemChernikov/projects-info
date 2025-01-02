package ru.projects.view.tasks;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.projects.model.Employee;
import ru.projects.model.dto.employee.EmployeeShortDto;
import ru.projects.model.dto.task.TaskFullDto;
import ru.projects.model.dto.task.TaskViewDto;
import ru.projects.model.enums.Priority;
import ru.projects.model.enums.Status;
import ru.projects.service.EmployeeService;
import ru.projects.service.TaskService;
import ru.projects.view.MainLayout;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@PageTitle("Tasks")
@Route(value = "pm-tasks/:taskID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed(value = {"ROLE_PM"})
@Menu(order = 6, icon = "line-awesome/svg/list-alt-solid.svg")
public class PMTasksView extends Div implements BeforeEnterObserver {

    private static final String TASK_ID = "taskID";
    private static final String TASK_EDIT_ROUTE_TEMPLATE = "pm-tasks/%s/edit";

    private final Grid<TaskViewDto> grid = new Grid<>(TaskViewDto.class, false);

    private TextField name;
    private TextArea description;
    private ComboBox<EmployeeShortDto> employee;
    private ComboBox<String> priority;
    private ComboBox<String> status;

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");

    private BeanValidationBinder<TaskFullDto> binder;

    private TaskFullDto task;

    private Employee authenticatedEmployee;

    private final TaskService taskService;
    private final EmployeeService employeeService;

    public PMTasksView(TaskService taskService, EmployeeService employeeService) {
        this.taskService = taskService;
        this.employeeService = employeeService;
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
        save.addClickListener(clickEvent -> updateTask());
        delete.addClickListener(clickEvent -> deleteTask());
    }

    private void updateTask() {
        try {
            if (this.task == null) {
                this.task = new TaskFullDto();
            }
            binder.writeBean(this.task);
            taskService.update(this.task);
            clearForm();
            refreshGrid();
            Notification.show("The task has been updated.", 3000, Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(PMTasksView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification.show(
                    "Error updating the task. Somebody else has updated the record while you were making changes.",
                    3000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("Failed to update the task. Check again that all values are valid",
                    3000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteTask() {
        try {
            if (this.task == null) {
                this.task = new TaskFullDto();
            }
            binder.writeBean(this.task);
            taskService.deleteById(this.task.getTaskId());
            clearForm();
            refreshGrid();
            Notification.show("The task has been removed.", 3000, Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(PMTasksView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification.show(
                    "Error updating the data. Somebody else has updated the record while you were making changes.",
                    3000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("Failed to delete task. Check again that all values are valid",
                    3000, Position.TOP_CENTER);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> employeeId = event.getRouteParameters().get(TASK_ID).map(Long::parseLong);
        if (employeeId.isPresent()) {
            Optional<TaskFullDto> optionalTaskFromBackend = taskService.getById(employeeId.get());
            if (optionalTaskFromBackend.isPresent()) {
                TaskFullDto taskFromBackend = optionalTaskFromBackend.get();
                setEmployeesToComboBox(taskFromBackend.getProject().getProjectId(), taskFromBackend.getTaskType());
                setPrioritiesToComboBox();
                setStatusesToComboBox();
                fillEditForm(taskFromBackend);
            } else {
                Notification.show(String.format("The requested employee was not found, ID = %s", employeeId.get()),
                        3000, Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(PMTasksView.class);
            }
        }
    }

    private void configureValidationBinder() {
        binder = new BeanValidationBinder<>(TaskFullDto.class);
        binder.forField(name)
                .asRequired("Task name is required")
                .withValidator(value -> !value.trim().isEmpty(), "Task name cannot be empty or spaces only")
                .bind(TaskFullDto::getName, TaskFullDto::setName);
        binder.forField(description)
                .asRequired("Description is required")
                .withValidator(value -> !value.trim().isEmpty(), "Description cannot be empty or spaces only")
                .bind(TaskFullDto::getDescription, TaskFullDto::setDescription);
        binder.forField(employee)
                .asRequired("Employee is required")
                .bind(TaskFullDto::getEmployee, TaskFullDto::setEmployee);
        binder.forField(priority)
                .asRequired("Priority is required")
                .bind(TaskFullDto::getPriority, TaskFullDto::setPriority);
        binder.forField(status)
                .asRequired("Status is required")
                .bind(TaskFullDto::getStatus, TaskFullDto::setStatus);
        binder.bindInstanceFields(this);
    }

    private void setRequiredFields() {
        name.setRequiredIndicatorVisible(true);
        description.setRequiredIndicatorVisible(true);
        employee.setRequiredIndicatorVisible(true);
        priority.setRequiredIndicatorVisible(true);
        status.setRequiredIndicatorVisible(true);
    }

    private void configureGrid() {
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("project").setAutoWidth(true);
        grid.addColumn("employee").setAutoWidth(true);
        grid.addColumn("taskType").setAutoWidth(true);
        grid.addColumn("priority").setAutoWidth(true);
        grid.addColumn("status").setAutoWidth(true);

        grid.addColumn(TaskDescriptionDetails.createToggleDetailsRenderer(grid));
        grid.setDetailsVisibleOnClick(false);
        grid.setItemDetailsRenderer(TaskDescriptionDetails.createTaskDetailsRenderer());

        grid.setItems(query -> taskService.getAllByProjects(
                        PageRequest.of(query.getPage(), query.getPageSize(),
                                VaadinSpringDataHelpers.toSpringDataSort(query)), authenticatedEmployee.getProjects())
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(TASK_EDIT_ROUTE_TEMPLATE, event.getValue().getTaskId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(PMTasksView.class);
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
        employee = new ComboBox<>("Employee");
        priority = new ComboBox<>("Priority");
        status = new ComboBox<>("Status");

        employee.setWidth("min-content");
        priority.setWidth("min-content");
        status.setWidth("min-content");

        setRequiredFields();
        formLayout.add(name, description, employee, priority, status);

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
        status.clear();
    }

    private void fillEditForm(TaskFullDto value) {
        this.task = value;
        binder.readBean(this.task);
    }

    private void setEmployeesToComboBox(Long projectId, String taskType) {
        Set<EmployeeShortDto> employees = employeeService.getAllEmployeesByProjectIdAndTaskType(projectId, taskType);
        employee.setItems(employees);
        employee.setItemLabelGenerator(EmployeeShortDto::getName);
    }

    private void setStatusesToComboBox() {
        status.setItems(List.of(Status.NEW.getDisplayName(), Status.IN_PROGRESS.getDisplayName(), Status.FINISHED.getDisplayName()));
    }

    private void setPrioritiesToComboBox() {
        priority.setItems(List.of(Priority.HIGH.getDisplayName(), Priority.MEDIUM.getDisplayName(), Priority.LOW.getDisplayName()));
    }
}