package ru.projects.view.tasks;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import ru.projects.model.Employee;
import ru.projects.model.dto.employee.EmployeeShortDto;
import ru.projects.model.dto.project.ProjectShortDto;
import ru.projects.model.dto.task.TaskCreateDto;
import ru.projects.model.enums.Priority;
import ru.projects.model.enums.TaskType;
import ru.projects.service.EmployeeService;
import ru.projects.service.ProjectService;
import ru.projects.service.TaskService;
import ru.projects.view.MainLayout;

import java.util.List;
import java.util.Set;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 20.10.2024
 */
@PageTitle("Add Task")
@Route(value = "add-task", layout = MainLayout.class)
@RolesAllowed(value = {"ROLE_PM"})
@Menu(order = 7, icon = "line-awesome/svg/pencil-alt-solid.svg")
@Slf4j
public class CreateTaskView extends Composite<VerticalLayout> {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final EmployeeService employeeService;

    private Employee authenticatedEmployee;

    private TextField name;
    private TextArea description;
    private ComboBox<ProjectShortDto> project;
    private ComboBox<EmployeeShortDto> employee;
    private ComboBox<String> taskType;
    private ComboBox<String> priority;

    private FormLayout formLayout2Col;
    private BeanValidationBinder<TaskCreateDto> binder;

    private TaskCreateDto taskCreateDto;

    public CreateTaskView(TaskService taskService, ProjectService projectService, EmployeeService employeeService) {
        this.taskService = taskService;
        this.projectService = projectService;
        this.employeeService = employeeService;
        authenticatedEmployee = employeeService.getCurrentEmployee();

        getContent().setWidth("100%");
        getContent().setHeight("100%");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);

        initLayout();
        initFormFields();
        configureValidationBinder();
        setRequiredFields();
        initButtons();
        setFieldsEnabled(false);
        taskType.setEnabled(false);
    }

    private void setFieldsEnabled(boolean enabled) {
        name.setEnabled(enabled);
        description.setEnabled(enabled);
        employee.setEnabled(enabled);
        priority.setEnabled(enabled);
    }

    private void saveTask() {
        log.info("VIEW: Add task");
        try {
            if (this.taskCreateDto == null) {
                this.taskCreateDto = new TaskCreateDto();
            }
            binder.writeBean(this.taskCreateDto);
            taskService.save(taskCreateDto);
            clearForm();
            log.info("VIEW: Task added");
            Notification.show("Task added successfully.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            log.error("VIEW: Failed to add task: {}", e.getMessage());
            Notification.show("Failed to add task. Check again that all values are valid",
                    3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void initButtons() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("50px");
        layoutRow.setAlignItems(Alignment.CENTER);
        layoutRow.setJustifyContentMode(JustifyContentMode.CENTER);

        Button buttonSave = new Button("Save", event -> saveTask());
        buttonSave.setWidth("min-content");
        buttonSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button buttonCancel = new Button("Cancel", event -> clearForm());
        buttonCancel.setWidth("min-content");

        layoutRow.add(buttonSave, buttonCancel);

        VerticalLayout layoutColumn2 = (VerticalLayout) getContent().getComponentAt(0);
        layoutColumn2.add(layoutRow);
    }

    private void initLayout() {
        VerticalLayout layoutColumn2 = new VerticalLayout();
        layoutColumn2.setWidth("1600px");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("1600px");

        H3 h3 = new H3("Task information");
        h3.setWidth("100%");

        formLayout2Col = new FormLayout();
        formLayout2Col.setWidth("100%");

        layoutColumn2.add(h3, formLayout2Col);

        getContent().add(layoutColumn2);
    }

    private void initFormFields() {
        name = new TextField("Task name");
        description = new TextArea("Description");
        initComboBoxes();
        addActionForChangeProject();
        addActionForChangeTaskType();
        formLayout2Col.add(project, taskType, name, priority, employee, description);
    }

    private void initComboBoxes() {
        project = new ComboBox<>("Project");
        employee = new ComboBox<>("Employee");
        taskType = new ComboBox<>("Task type");
        priority = new ComboBox<>("Priority");
        setWidthToComboBoxes();
        setProjectsToComboBox();
        setTaskTypesToComboBox();
        setPrioritiesToComboBox();
    }

    private void setWidthToComboBoxes() {
        project.setWidth("min-content");
        employee.setWidth("min-content");
        taskType.setWidth("min-content");
        priority.setWidth("min-content");
    }

    private void addActionForChangeProject() {
        project.addValueChangeListener(event -> {
            ProjectShortDto selectedProject = event.getValue();
            if (selectedProject != null && taskType.getValue() == null) {
                taskType.setEnabled(true);
            } else if (selectedProject != null && taskType.getValue() != null) {
                setEmployeesToComboBox(selectedProject.getProjectId(), taskType.getValue());
            } else {
                setFieldsEnabled(false);
                taskType.setEnabled(false);
            }
        });
    }

    private void addActionForChangeTaskType() {
        taskType.addValueChangeListener(event -> {
            String selectedTaskType = event.getValue();
            if (selectedTaskType != null) {
                setFieldsEnabled(true);
                setEmployeesToComboBox(project.getValue().getProjectId(), taskType.getValue());
            } else {
                setFieldsEnabled(false);
            }
        });
    }

    private void setProjectsToComboBox() {
        Set<ProjectShortDto> projectShortDtos = projectService
                .getAllProjectShortDtoByEmployeeId(authenticatedEmployee.getEmployeeId());
        project.setItems(projectShortDtos);
        project.setItemLabelGenerator(ProjectShortDto::getName);
    }

    private void setEmployeesToComboBox(Long projectId, String taskType) {
        Set<EmployeeShortDto> employeesByProjectId = employeeService
                .getAllEmployeesByProjectIdAndTaskType(projectId, taskType);
        employee.setItems(employeesByProjectId);
        employee.setItemLabelGenerator(EmployeeShortDto::getName);
    }

    private void setTaskTypesToComboBox() {
        taskType.setItems(Set.of(TaskType.DEVELOPMENT.getDisplayName(), TaskType.TESTING.getDisplayName(),
                TaskType.DEV_OPS.getDisplayName(), TaskType.DATA_ANALYSIS.getDisplayName(), TaskType.DATA_SCIENCE.getDisplayName()));
    }

    private void setPrioritiesToComboBox() {
        priority.setItems(List.of(Priority.HIGH.getDisplayName(), Priority.MEDIUM.getDisplayName(), Priority.LOW.getDisplayName()));
    }

    private void configureValidationBinder() {
        binder = new BeanValidationBinder<>(TaskCreateDto.class);
        binder.forField(project)
                .asRequired("Project is required")
                .bind(TaskCreateDto::getProject, TaskCreateDto::setProject);
        binder.forField(name)
                .asRequired("Task name is required")
                .withValidator(value -> !value.trim().isEmpty(), "Task name cannot be empty or spaces only")
                .bind(TaskCreateDto::getName, TaskCreateDto::setName);
        binder.forField(description)
                .asRequired("Description is required")
                .withValidator(value -> !value.trim().isEmpty(), "Description cannot be empty or spaces only")
                .bind(TaskCreateDto::getDescription, TaskCreateDto::setDescription);
        binder.forField(taskType)
                .asRequired("Task type is required")
                .bind(TaskCreateDto::getTaskType, TaskCreateDto::setTaskType);
        binder.forField(priority)
                .asRequired("Priority is required")
                .bind(TaskCreateDto::getPriority, TaskCreateDto::setPriority);
        binder.forField(employee)
                .asRequired("Employee is required")
                .bind(TaskCreateDto::getEmployee, TaskCreateDto::setEmployee);
        binder.bindInstanceFields(this);
    }

    private void setRequiredFields() {
        name.setRequiredIndicatorVisible(true);
        description.setRequiredIndicatorVisible(true);
        project.setRequiredIndicatorVisible(true);
        taskType.setRequiredIndicatorVisible(true);
        priority.setRequiredIndicatorVisible(true);
    }

    private void clearForm() {
        this.taskCreateDto = null;
        binder.readBean(this.taskCreateDto);
    }

}
