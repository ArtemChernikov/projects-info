package ru.projects.view.projects;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.RolesAllowed;
import ru.projects.model.dto.employee.EmployeeShortDto;
import ru.projects.model.dto.project.ProjectCreateDto;
import ru.projects.service.EmployeeService;
import ru.projects.service.ProjectService;
import ru.projects.view.MainLayout;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.projects.util.Constants.AQA_ENGINEER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.BACKEND_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.DATA_ANALYST_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.DATA_SCIENTIST_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.DEV_OPS_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.FRONTEND_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.FULLSTACK_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.PROJECT_MANAGER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.QA_ENGINEER_SPECIALIZATION_NAME;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 20.10.2024
 */
@PageTitle("Create Project")
@Route(value = "create-project", layout = MainLayout.class)
@RolesAllowed(value = {"ROLE_ADMIN", "ROLE_PM"})
@Menu(order = 5, icon = "line-awesome/svg/user.svg")
public class CreateProjectView extends Composite<VerticalLayout> {

    private final ProjectService projectService;
    private final EmployeeService employeeService;
    private TextField name;
    private DatePicker startDate;
    private MultiSelectComboBox<EmployeeShortDto> projectManagersComboBox;
    private MultiSelectComboBox<EmployeeShortDto> backendDevelopersComboBox;
    private MultiSelectComboBox<EmployeeShortDto> frontendDevelopersComboBox;
    private MultiSelectComboBox<EmployeeShortDto> fullstackDevelopersComboBox;
    private MultiSelectComboBox<EmployeeShortDto> qaEngineersComboBox;
    private MultiSelectComboBox<EmployeeShortDto> aqaEngineersComboBox;
    private MultiSelectComboBox<EmployeeShortDto> devOpsComboBox;
    private MultiSelectComboBox<EmployeeShortDto> dataScientistsComboBox;
    private MultiSelectComboBox<EmployeeShortDto> dataAnalystsComboBox;
    private FormLayout formLayout2Col;
    private BeanValidationBinder<ProjectCreateDto> binder;

    private ProjectCreateDto projectCreateDto;

    public CreateProjectView(EmployeeService employeeService, ProjectService projectService) {
        this.employeeService = employeeService;
        this.projectService = projectService;

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

    private void saveProject() {
        try {
            if (this.projectCreateDto == null) {
                this.projectCreateDto = new ProjectCreateDto();
            }
            binder.writeBean(this.projectCreateDto);
            setEmployeesToProject();
            projectService.save(projectCreateDto);
            clearForm();
            Notification.show("Project saved successfully.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            Notification.show("Failed to create project. Check again that all values are valid",
                    3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void setEmployeesToProject() {
        Set<EmployeeShortDto> employees = Stream.of(
                        projectManagersComboBox.getValue(),
                        backendDevelopersComboBox.getValue(),
                        frontendDevelopersComboBox.getValue(),
                        fullstackDevelopersComboBox.getValue(),
                        qaEngineersComboBox.getValue(),
                        aqaEngineersComboBox.getValue(),
                        devOpsComboBox.getValue(),
                        dataScientistsComboBox.getValue(),
                        dataAnalystsComboBox.getValue()
                )
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        projectCreateDto.setEmployees(employees);
    }

    private void initButtons() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("50px");
        layoutRow.setAlignItems(Alignment.CENTER);
        layoutRow.setJustifyContentMode(JustifyContentMode.CENTER);

        Button buttonSave = new Button("Save", event -> saveProject());
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

        H3 h3 = new H3("Project Information");
        h3.setWidth("100%");

        formLayout2Col = new FormLayout();
        formLayout2Col.setWidth("100%");

        layoutColumn2.add(h3, formLayout2Col);

        getContent().add(layoutColumn2);
    }

    private void initFormFields() {
        name = new TextField("Project Name");
        startDate = new DatePicker("Start Date");
        initComboBoxes();
        formLayout2Col.add(name, startDate, projectManagersComboBox, backendDevelopersComboBox, frontendDevelopersComboBox,
                fullstackDevelopersComboBox, qaEngineersComboBox, aqaEngineersComboBox, devOpsComboBox,
                dataScientistsComboBox, dataAnalystsComboBox);
    }

    private void initComboBoxes() {
        projectManagersComboBox = new MultiSelectComboBox<>("Project Managers");
        backendDevelopersComboBox = new MultiSelectComboBox<>("Backend Developers");
        frontendDevelopersComboBox = new MultiSelectComboBox<>("Frontend Developers");
        fullstackDevelopersComboBox = new MultiSelectComboBox<>("Fullstack Developers");
        qaEngineersComboBox = new MultiSelectComboBox<>("QA Engineers");
        aqaEngineersComboBox = new MultiSelectComboBox<>("AQA Engineers");
        devOpsComboBox = new MultiSelectComboBox<>("DevOps");
        dataScientistsComboBox = new MultiSelectComboBox<>("Data Scientists");
        dataAnalystsComboBox = new MultiSelectComboBox<>("Data Analysts");
        setWidthToComboBoxes();
        setEmployeesToComboBoxes();
    }

    private void setWidthToComboBoxes() {
        projectManagersComboBox.setWidth("min-content");
        backendDevelopersComboBox.setWidth("min-content");
        frontendDevelopersComboBox.setWidth("min-content");
        fullstackDevelopersComboBox.setWidth("min-content");
        qaEngineersComboBox.setWidth("min-content");
        aqaEngineersComboBox.setWidth("min-content");
        devOpsComboBox.setWidth("min-content");
        dataScientistsComboBox.setWidth("min-content");
        dataAnalystsComboBox.setWidth("min-content");
    }

    private void setEmployeesToComboBoxes() {
        Map<String, List<EmployeeShortDto>> employees = employeeService.getAllEmployeesBySpecialization();
        setMultiSelectComboBoxItems(projectManagersComboBox, PROJECT_MANAGER_SPECIALIZATION_NAME, employees);
        setMultiSelectComboBoxItems(backendDevelopersComboBox, BACKEND_DEVELOPER_SPECIALIZATION_NAME, employees);
        setMultiSelectComboBoxItems(frontendDevelopersComboBox, FRONTEND_DEVELOPER_SPECIALIZATION_NAME, employees);
        setMultiSelectComboBoxItems(fullstackDevelopersComboBox, FULLSTACK_DEVELOPER_SPECIALIZATION_NAME, employees);
        setMultiSelectComboBoxItems(qaEngineersComboBox, QA_ENGINEER_SPECIALIZATION_NAME, employees);
        setMultiSelectComboBoxItems(aqaEngineersComboBox, AQA_ENGINEER_SPECIALIZATION_NAME, employees);
        setMultiSelectComboBoxItems(devOpsComboBox, DEV_OPS_SPECIALIZATION_NAME, employees);
        setMultiSelectComboBoxItems(dataScientistsComboBox, DATA_SCIENTIST_SPECIALIZATION_NAME, employees);
        setMultiSelectComboBoxItems(dataAnalystsComboBox, DATA_ANALYST_SPECIALIZATION_NAME, employees);
    }

    private void configureValidationBinder() {
        binder = new BeanValidationBinder<>(ProjectCreateDto.class);
        binder.forField(name)
                .asRequired("Project Name is required")
                .bind(ProjectCreateDto::getName, ProjectCreateDto::setName);
        binder.forField(startDate)
                .asRequired("Start Date is required")
                .bind(ProjectCreateDto::getStartDate, ProjectCreateDto::setStartDate);
        binder.bindInstanceFields(this);
    }

    private void setRequiredFields() {
        name.setRequiredIndicatorVisible(true);
        startDate.setRequiredIndicatorVisible(true);
    }

    private void clearForm() {
        this.projectCreateDto = null;
        binder.readBean(this.projectCreateDto);
        projectManagersComboBox.clear();
        backendDevelopersComboBox.clear();
        frontendDevelopersComboBox.clear();
        fullstackDevelopersComboBox.clear();
        qaEngineersComboBox.clear();
        aqaEngineersComboBox.clear();
        devOpsComboBox.clear();
        dataScientistsComboBox.clear();
        dataAnalystsComboBox.clear();
    }

    private void setMultiSelectComboBoxItems(MultiSelectComboBox<EmployeeShortDto> comboBox, String specializationName,
                                             Map<String, List<EmployeeShortDto>> employees) {
        if (employees.containsKey(specializationName)) {
            comboBox.setItems(employees.get(specializationName));
            comboBox.setItemLabelGenerator(EmployeeShortDto::getName);
        }
    }
}
