package ru.projects.views.employees;

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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.projects.model.dto.EmployeeFullDto;
import ru.projects.model.dto.SpecializationDto;
import ru.projects.services.EmployeeService;
import ru.projects.services.SpecializationService;
import ru.projects.views.MainLayout;

@PageTitle("Admin Employees")
@Route(value = "admin-employees/:employeeID?/:action?(edit)", layout = MainLayout.class)
//@RolesAllowed("ADMIN")
public class AdminEmployeesView extends Div implements BeforeEnterObserver {

    private static final String EMPLOYEE_ID = "employeeID";
    private static final String EMPLOYEE_EDIT_ROUTE_TEMPLATE = "admin-employees/%s/edit";

    private final Grid<EmployeeFullDto> grid = new Grid<>(EmployeeFullDto.class, false);

    private TextField firstName;
    private TextField lastName;
    private TextField patronymicName;
    private DatePicker dateOfBirth;
    private TextField phone;
    private EmailField email;
    private TextField login;
    private PasswordField password;
    private ComboBox<String> specializationsComboBox;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private BeanValidationBinder<EmployeeFullDto> binder;

    private EmployeeFullDto employee;

    private final EmployeeService employeeService;
    private final SpecializationService specializationService;

    public AdminEmployeesView(EmployeeService employeeService, SpecializationService specializationService) {
        this.employeeService = employeeService;
        this.specializationService = specializationService;
        addClassNames("admin-employees-view");
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
        save.addClickListener(clickEvent -> updateEmployee());
        delete.addClickListener(clickEvent -> deleteEmployee());
    }

    private void updateEmployee() {
        try {
            if (this.employee == null) {
                this.employee = new EmployeeFullDto();
            }
            binder.writeBean(this.employee);
            employeeService.update(this.employee);
            clearForm();
            refreshGrid();
            Notification.show("The employee has been updated.", 3000, Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(AdminEmployeesView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification.show(
                    "Error updating the data. Somebody else has updated the record while you were making changes.",
                    3000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("Failed to update the data. Check again that all values are valid",
                    3000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteEmployee() {
        try {
            if (this.employee == null) {
                this.employee = new EmployeeFullDto();
            }
            binder.writeBean(this.employee);
            employeeService.deleteById(this.employee.getEmployeeId());
            clearForm();
            refreshGrid();
            Notification.show("The employee has been removed.", 3000, Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(AdminEmployeesView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
        Notification.show(
                    "Error updating the data. Somebody else has updated the record while you were making changes.",
                    3000, Notification.Position.TOP_CENTER). addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("Failed to delete employee. Check again that all values are valid",
                    3000, Notification.Position.TOP_CENTER);
        }
    }

    private void configureValidationBinder() {
        binder = new BeanValidationBinder<>(EmployeeFullDto.class);
        binder.forField(firstName)
                .asRequired("First Name is required")
                .bind(EmployeeFullDto::getFirstName, EmployeeFullDto::setFirstName);
        binder.forField(lastName)
                .asRequired("Last Name is required")
                .bind(EmployeeFullDto::getLastName, EmployeeFullDto::setLastName);
        binder.forField(patronymicName)
                .asRequired("Patronymic Name is required")
                .bind(EmployeeFullDto::getPatronymicName, EmployeeFullDto::setPatronymicName);
        binder.forField(dateOfBirth)
                .asRequired("Date of Birth is required")
                .bind(EmployeeFullDto::getDateOfBirth, EmployeeFullDto::setDateOfBirth);
        binder.forField(phone)
                .asRequired("Phone is required")
                .bind(EmployeeFullDto::getPhone, EmployeeFullDto::setPhone);
        binder.forField(email)
                .asRequired("Email is required")
                .bind(EmployeeFullDto::getEmail, EmployeeFullDto::setEmail);
        binder.forField(login)
                .asRequired("Login is required")
                .bind(EmployeeFullDto::getLogin, EmployeeFullDto::setLogin);
        binder.forField(password)
                .asRequired("Password is required")
                .bind(EmployeeFullDto::getPassword, EmployeeFullDto::setPassword);
        binder.forField(specializationsComboBox)
                .asRequired("Specialization is required")
                .bind(EmployeeFullDto::getSpecialization, EmployeeFullDto::setSpecialization);
        binder.bindInstanceFields(this);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> employeeId = event.getRouteParameters().get(EMPLOYEE_ID).map(Long::parseLong);
        if (employeeId.isPresent()) {
            Optional<EmployeeFullDto> employeeFromBackend = employeeService.getById(employeeId.get());
            if (employeeFromBackend.isPresent()) {
                populateForm(employeeFromBackend.get());
            } else {
                Notification.show(String.format("The requested employee was not found, ID = %s", employeeId.get()),
                        3000, Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(AdminEmployeesView.class);
            }
        }
    }

    private void configureGrid() {
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("patronymicName").setAutoWidth(true);
        grid.addColumn("dateOfBirth").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("login").setAutoWidth(true);
        grid.addColumn("specialization").setAutoWidth(true);
        grid.setItems(query -> employeeService.getAll(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(EMPLOYEE_EDIT_ROUTE_TEMPLATE, event.getValue().getEmployeeId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AdminEmployeesView.class);
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
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        patronymicName = new TextField("Patronymic Name");
        dateOfBirth = new DatePicker("Date Of Birth");
        phone = new TextField("Phone");
        email = new EmailField("Email");
        login = new TextField("Login");
        password = new PasswordField("Password");
        specializationsComboBox = new ComboBox<>("Specialization");
        specializationsComboBox.setWidth("min-content");
        setSpecializations();
        setRequiredFiles();
        formLayout.add(firstName, lastName, patronymicName, dateOfBirth, phone, email, login, password, specializationsComboBox);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void setRequiredFiles() {
        firstName.setRequiredIndicatorVisible(true);
        lastName.setRequiredIndicatorVisible(true);
        patronymicName.setRequiredIndicatorVisible(true);
        dateOfBirth.setRequiredIndicatorVisible(true);
        phone.setRequiredIndicatorVisible(true);
        email.setRequiredIndicatorVisible(true);
        login.setRequiredIndicatorVisible(true);
        password.setRequiredIndicatorVisible(true);
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
        populateForm(null);
        specializationsComboBox.clear();
    }

    private void populateForm(EmployeeFullDto value) {
        this.employee = value;
        binder.readBean(this.employee);
    }

    private void setSpecializations() {
        List<String> specializations = specializationService.getAll().stream()
                .map(SpecializationDto::getSpecializationName).toList();
        specializationsComboBox.setItems(specializations);
    }
}