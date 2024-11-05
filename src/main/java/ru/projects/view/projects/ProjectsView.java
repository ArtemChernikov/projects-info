package ru.projects.view.projects;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
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
import ru.projects.model.dto.EmployeeShortDto;
import ru.projects.model.dto.ProjectFullDto;
import ru.projects.model.enums.Status;
import ru.projects.service.EmployeeService;
import ru.projects.service.ProjectService;
import ru.projects.view.MainLayout;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ru.projects.util.Constants.AQA_ENGINEER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.BACKEND_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.DATA_ANALYST_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.DATA_SCIENTIST_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.DEV_OPS_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.FRONTEND_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.FULLSTACK_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.PROJECT_MANAGER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.QA_ENGINEER_SPECIALIZATION_NAME;

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
    private ComboBox<String> status;
    private MultiSelectComboBox<EmployeeShortDto> projectManagers;
    private MultiSelectComboBox<EmployeeShortDto> backendDevelopers;
    private MultiSelectComboBox<EmployeeShortDto> frontendDevelopers;
    private MultiSelectComboBox<EmployeeShortDto> fullstackDevelopers;
    private MultiSelectComboBox<EmployeeShortDto> qaEngineers;
    private MultiSelectComboBox<EmployeeShortDto> aqaEngineers;
    private MultiSelectComboBox<EmployeeShortDto> devOps;
    private MultiSelectComboBox<EmployeeShortDto> dataScientists;
    private MultiSelectComboBox<EmployeeShortDto> dataAnalysts;

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");

    private BeanValidationBinder<ProjectFullDto> binder;

    private ProjectFullDto projectFullDto;

    private final ProjectService projectService;
    private final EmployeeService employeeService;

    public ProjectsView(ProjectService projectService, EmployeeService employeeService) {
        this.projectService = projectService;
        this.employeeService = employeeService;
        addClassNames("projects-view");
        createUI();
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
        binder.forField(status)
                .asRequired("Project Status is required")
                .bind(ProjectFullDto::getStatus, ProjectFullDto::setStatus);
        binder.bindInstanceFields(this);
    }

    private void setRequiredFields() {
        name.setRequiredIndicatorVisible(true);
        startDate.setRequiredIndicatorVisible(true);
        status.setRequiredIndicatorVisible(true);
    }

    private void configureGrid() {
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("startDate").setAutoWidth(true);
        grid.addColumn("endDate").setAutoWidth(true);
        grid.addColumn("status").setAutoWidth(true);

        grid.addColumn(ProjectEmployeeDetails.createToggleDetailsRenderer(grid));

        grid.setDetailsVisibleOnClick(false);
        grid.setItemDetailsRenderer(ProjectEmployeeDetails.createProjectDetailsRenderer());

        grid.setItems(query -> projectService.getAll(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

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
        initializeFields();
        setWidthsToFields("min-content");

        setStatusesToComboBox();
        setEmployeesToComboBox();
        setRequiredFields();

        formLayout.add(name, startDate, endDate, status, projectManagers, backendDevelopers,
                frontendDevelopers, fullstackDevelopers, qaEngineers, aqaEngineers,
                devOps, dataScientists, dataAnalysts);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void initializeFields() {
        name = new TextField("Project Name");
        startDate = new DatePicker("Start Date");
        endDate = new DatePicker("End Date");
        status = new ComboBox<>("Status");
        projectManagers = new MultiSelectComboBox<>("Project Manager");
        backendDevelopers = new MultiSelectComboBox<>("Backend Developers");
        frontendDevelopers = new MultiSelectComboBox<>("Frontend Developers");
        fullstackDevelopers = new MultiSelectComboBox<>("Fullstack Developer");
        qaEngineers = new MultiSelectComboBox<>("QA Engineer");
        aqaEngineers = new MultiSelectComboBox<>("AQA Engineer");
        devOps = new MultiSelectComboBox<>("DevOps");
        dataScientists = new MultiSelectComboBox<>("Data Scientist");
        dataAnalysts = new MultiSelectComboBox<>("Data Analysts");
    }

    private void setWidthsToFields(String width) {
        status.setWidth(width);
        projectManagers.setWidth(width);
        backendDevelopers.setWidth(width);
        frontendDevelopers.setWidth(width);
        fullstackDevelopers.setWidth(width);
        qaEngineers.setWidth(width);
        aqaEngineers.setWidth(width);
        devOps.setWidth(width);
        dataScientists.setWidth(width);
        dataAnalysts.setWidth(width);
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

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        fillEditForm(null);
        status.clear();
        projectManagers.clear();
        backendDevelopers.clear();
        frontendDevelopers.clear();
        fullstackDevelopers.clear();
        qaEngineers.clear();
        aqaEngineers.clear();
        devOps.clear();
        dataScientists.clear();
        dataAnalysts.clear();
    }

    private void fillEditForm(ProjectFullDto value) {
        this.projectFullDto = value;
        binder.readBean(this.projectFullDto);
    }

    private void setStatusesToComboBox() {
        status.setItems(List.of(Status.NEW.getDisplayName(), Status.IN_PROGRESS.getDisplayName(),
                Status.FINISHED.getDisplayName()));
    }

    private void setEmployeesToComboBox() {
        Map<String, List<EmployeeShortDto>> allEmployeesBySpecialization = employeeService.getAllEmployeesBySpecialization();

        setComboBoxItems(projectManagers, allEmployeesBySpecialization.get(PROJECT_MANAGER_SPECIALIZATION_NAME));
        setComboBoxItems(backendDevelopers, allEmployeesBySpecialization.get(BACKEND_DEVELOPER_SPECIALIZATION_NAME));
        setComboBoxItems(frontendDevelopers, allEmployeesBySpecialization.get(FRONTEND_DEVELOPER_SPECIALIZATION_NAME));
        setComboBoxItems(fullstackDevelopers, allEmployeesBySpecialization.get(FULLSTACK_DEVELOPER_SPECIALIZATION_NAME));
        setComboBoxItems(qaEngineers, allEmployeesBySpecialization.get(QA_ENGINEER_SPECIALIZATION_NAME));
        setComboBoxItems(aqaEngineers, allEmployeesBySpecialization.get(AQA_ENGINEER_SPECIALIZATION_NAME));
        setComboBoxItems(devOps, allEmployeesBySpecialization.get(DEV_OPS_SPECIALIZATION_NAME));
        setComboBoxItems(dataScientists, allEmployeesBySpecialization.get(DATA_SCIENTIST_SPECIALIZATION_NAME));
        setComboBoxItems(dataAnalysts, allEmployeesBySpecialization.get(DATA_ANALYST_SPECIALIZATION_NAME));
    }

    private void setComboBoxItems(MultiSelectComboBox<EmployeeShortDto> comboBox, List<EmployeeShortDto> employees) {
        if (employees != null) {
            comboBox.setItems(employees);
            comboBox.setItemLabelGenerator(EmployeeShortDto::getName);
        }
    }
}