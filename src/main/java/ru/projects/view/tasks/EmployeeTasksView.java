package ru.projects.view.tasks;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import ru.projects.model.Employee;
import ru.projects.model.dto.task.TaskViewDto;
import ru.projects.model.enums.Status;
import ru.projects.service.EmployeeService;
import ru.projects.service.TaskService;
import ru.projects.view.MainLayout;

@PageTitle("My tasks")
@Route(value = "employee-tasks", layout = MainLayout.class)
@RolesAllowed(value = {"ROLE_USER", "ROLE_DEV", "ROLE_TEST"})
@Menu(order = 9, icon = "line-awesome/svg/list-alt-solid.svg")
@Slf4j
public class EmployeeTasksView extends Div  {

    private final Grid<TaskViewDto> grid = new Grid<>(TaskViewDto.class, false);

    private Employee authenticatedEmployee;

    private final TaskService taskService;

    public EmployeeTasksView(TaskService taskService, EmployeeService employeeService) {
        this.taskService = taskService;
        authenticatedEmployee = employeeService.getCurrentEmployee();
        addClassNames("employee-tasks-view");
        createUI();
    }

    private void createUI() {
        SplitLayout splitLayout = new SplitLayout();
        createGridLayout(splitLayout);
        add(splitLayout);
        configureGrid();
    }

    private void configureGrid() {
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("project").setAutoWidth(true);
        grid.addColumn("employee").setAutoWidth(true);
        grid.addColumn("taskType").setAutoWidth(true);
        grid.addColumn("priority").setAutoWidth(true);
        grid.addColumn("status").setAutoWidth(true);

        grid.addComponentColumn(task -> {
            ComboBox<String> statusComboBox = new ComboBox<>();
            statusComboBox.setItems(Status.NEW.getDisplayName(), Status.IN_PROGRESS.getDisplayName(),
                    Status.FINISHED.getDisplayName());
            statusComboBox.setValue(task.getStatus());
            statusComboBox.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    task.setStatus(event.getValue());
                    taskService.updateStatusById(task.getTaskId(), event.getValue());
                    log.info("VIEW: Task status updated.");
                    Notification.show("Status updated", 3000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                    refreshGrid();
                }
            });
            return statusComboBox;
        }).setHeader("Change status").setAutoWidth(true);

        grid.addColumn(TaskDescriptionDetails.createToggleDetailsRenderer(grid));
        grid.setDetailsVisibleOnClick(false);
        grid.setItemDetailsRenderer(TaskDescriptionDetails.createTaskDetailsRenderer());

        refreshGrid();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    }

    private void refreshGrid() {
        grid.setItems(query -> taskService.getAllByEmployeeId(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                authenticatedEmployee.getEmployeeId()
        ).stream());
        grid.getDataProvider().refreshAll();
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }
}