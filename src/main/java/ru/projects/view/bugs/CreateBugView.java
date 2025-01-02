package ru.projects.view.bugs;

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
import ru.projects.model.Employee;
import ru.projects.model.dto.bug.BugCreateDto;
import ru.projects.model.dto.project.ProjectShortDto;
import ru.projects.model.enums.Priority;
import ru.projects.service.BugService;
import ru.projects.service.EmployeeService;
import ru.projects.service.ProjectService;
import ru.projects.view.MainLayout;

import java.util.List;
import java.util.Set;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 10.11.2024
 */
@PageTitle("Create bug")
@Route(value = "create-bug", layout = MainLayout.class)
@RolesAllowed(value = {"ROLE_TEST"})
@Menu(order = 11, icon = "line-awesome/svg/bug-solid.svg")
public class CreateBugView extends Composite<VerticalLayout> {

    private final BugService bugService;
    private final ProjectService projectService;

    private Employee authenticatedEmployee;

    private TextField name;
    private TextArea description;
    private ComboBox<ProjectShortDto> project;
    private ComboBox<String> priority;

    private FormLayout formLayout2Col;
    private BeanValidationBinder<BugCreateDto> binder;

    private BugCreateDto bugCreateDto;

    public CreateBugView(BugService bugService, ProjectService projectService,
                         EmployeeService employeeService) {
        this.bugService = bugService;
        this.projectService = projectService;
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
    }

    private void saveBug() {
        try {
            if (this.bugCreateDto == null) {
                this.bugCreateDto = new BugCreateDto();
            }
            binder.writeBean(this.bugCreateDto);
            bugService.save(bugCreateDto);
            clearForm();
            Notification.show("Bug saved successfully.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            Notification.show("Failed to create bug. Check again that all values are valid",
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

        Button buttonSave = new Button("Save", event -> saveBug());
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

        H3 h3 = new H3("Bug information");
        h3.setWidth("100%");

        formLayout2Col = new FormLayout();
        formLayout2Col.setWidth("100%");

        layoutColumn2.add(h3, formLayout2Col);

        getContent().add(layoutColumn2);
    }

    private void initFormFields() {
        name = new TextField("Name");
        description = new TextArea("Description");
        initComboBoxes();
        formLayout2Col.add(project, name, priority, description);
    }

    private void initComboBoxes() {
        project = new ComboBox<>("Project");
        priority = new ComboBox<>("Priority");
        setWidthToComboBoxes();
        setProjectsToComboBox();
        setPrioritiesToComboBox();
    }

    private void setWidthToComboBoxes() {
        project.setWidth("min-content");
        priority.setWidth("min-content");
    }

    private void setProjectsToComboBox() {
        Set<ProjectShortDto> projectShortDtos = projectService
                .getAllProjectShortDtoByEmployeeId(authenticatedEmployee.getEmployeeId());
        project.setItems(projectShortDtos);
        project.setItemLabelGenerator(ProjectShortDto::getName);
    }

    private void setPrioritiesToComboBox() {
        priority.setItems(List.of(Priority.HIGH.getDisplayName(), Priority.MEDIUM.getDisplayName(), Priority.LOW.getDisplayName()));
    }

    private void configureValidationBinder() {
        binder = new BeanValidationBinder<>(BugCreateDto.class);
        binder.forField(project)
                .asRequired("Project is required")
                .bind(BugCreateDto::getProject, BugCreateDto::setProject);
        binder.forField(name)
                .asRequired("Bug name is required")
                .withValidator(value -> !value.trim().isEmpty(), "Bug name cannot be empty or spaces only")
                .bind(BugCreateDto::getName, BugCreateDto::setName);
        binder.forField(description)
                .asRequired("Description is required")
                .withValidator(value -> !value.trim().isEmpty(), "Description cannot be empty or spaces only")
                .bind(BugCreateDto::getDescription, BugCreateDto::setDescription);
        binder.forField(priority)
                .asRequired("Priority is required")
                .bind(BugCreateDto::getPriority, BugCreateDto::setPriority);
        binder.bindInstanceFields(this);
    }

    private void setRequiredFields() {
        name.setRequiredIndicatorVisible(true);
        description.setRequiredIndicatorVisible(true);
        project.setRequiredIndicatorVisible(true);
        priority.setRequiredIndicatorVisible(true);
    }

    private void clearForm() {
        this.bugCreateDto = null;
        binder.readBean(this.bugCreateDto);
    }

}
