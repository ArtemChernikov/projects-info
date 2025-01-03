package ru.projects.view.employees;

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
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.projects.model.dto.employee.EmployeeFullDto;
import ru.projects.service.EmployeeService;
import ru.projects.service.SpecializationService;
import ru.projects.view.MainLayout;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@PageTitle("Employees")
@Route(value = "edit-employees/:employeeID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed(value = {"ROLE_ADMIN"})
@Menu(order = 2, icon = "line-awesome/svg/users-cog-solid.svg")
@Slf4j
public class EditEmployeesView extends Div implements BeforeEnterObserver {

    private static final String EMPLOYEE_ID = "employeeID";
    private static final String EMPLOYEE_EDIT_ROUTE_TEMPLATE = "edit-employees/%s/edit";

    private final Grid<EmployeeFullDto> grid = new Grid<>(EmployeeFullDto.class, false);

    private TextField firstName;
    private TextField lastName;
    private TextField patronymicName;
    private DatePicker dateOfBirth;
    private TextField phone;
    private EmailField email;
    private TextField username;
    private PasswordField password;
    private ComboBox<String> specializationsComboBox;

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");

    private BeanValidationBinder<EmployeeFullDto> binder;

    private EmployeeFullDto employee;

    private final EmployeeService employeeService;
    private final SpecializationService specializationService;

    public EditEmployeesView(EmployeeService employeeService, SpecializationService specializationService) {
        this.employeeService = employeeService;
        this.specializationService = specializationService;
        addClassNames("edit-employees-view");
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
        log.info("VIEW: Updating employee");
        try {
            if (this.employee == null) {
                this.employee = new EmployeeFullDto();
            }
            binder.writeBean(this.employee);
            employeeService.update(this.employee);
            clearForm();
            refreshGrid();
            log.info("VIEW: Employee updated");
            Notification.show("The employee has been updated.", 3000, Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(EditEmployeesView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            log.error("VIEW: Error updating the employee: {}", exception.getMessage());
            Notification.show(
                    "Error updating the employee. Somebody else has updated the record while you were making changes.",
                    3000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            log.error("VIEW: Error updating the employee: {}", validationException.getMessage());
            Notification.show("Failed to update the employee. Check again that all values are valid",
                    3000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteEmployee() {
        log.info("VIEW: Deleting employee");
        try {
            if (this.employee == null) {
                this.employee = new EmployeeFullDto();
            }
            binder.writeBean(this.employee);
            employeeService.deleteById(this.employee.getEmployeeId());
            clearForm();
            refreshGrid();
            log.info("VIEW: Employee deleted.");
            Notification.show("The employee has been removed.", 3000, Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(EditEmployeesView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            log.error("VIEW: Error deleting the employee: {}", exception.getMessage());
            Notification.show(
                    "Error updating the data. Somebody else has updated the record while you were making changes.",
                    3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            log.error("VIEW: Error deleting the employee: {}", validationException.getMessage());
            Notification.show("Failed to delete employee. Check again that all values are valid",
                    3000, Notification.Position.TOP_CENTER);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> employeeId = event.getRouteParameters().get(EMPLOYEE_ID).map(Long::parseLong);
        if (employeeId.isPresent()) {
            Optional<EmployeeFullDto> employeeFromBackend = employeeService.getById(employeeId.get());
            if (employeeFromBackend.isPresent()) {
                fillEditForm(employeeFromBackend.get());
            } else {
                Notification.show(String.format("The requested employee was not found, ID = %s", employeeId.get()),
                        3000, Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(EditEmployeesView.class);
            }
        }
    }

    private void configureValidationBinder() {
        binder = new BeanValidationBinder<>(EmployeeFullDto.class);
        binder.forField(firstName)
                .asRequired("First name is required")
                .withValidator(value -> !value.trim().isEmpty(), "First name cannot be empty or spaces only")
                .withValidator(value -> value.matches("^[a-zA-Zа-яА-ЯёЁ]+$"), "First name must contain only letters")
                .bind(EmployeeFullDto::getFirstName, EmployeeFullDto::setFirstName);
        binder.forField(lastName)
                .asRequired("Last name is required")
                .withValidator(value -> !value.trim().isEmpty(), "Last name cannot be empty or spaces only")
                .withValidator(value -> value.matches("^[a-zA-Zа-яА-ЯёЁ]+$"), "Last name must contain only letters")
                .bind(EmployeeFullDto::getLastName, EmployeeFullDto::setLastName);
        binder.forField(patronymicName)
                .asRequired("Patronymic name is required")
                .withValidator(value -> !value.trim().isEmpty(), "Patronymic name cannot be empty or spaces only")
                .withValidator(value -> value.matches("^[a-zA-Zа-яА-ЯёЁ]+$"), "Patronymic name must contain only letters")
                .bind(EmployeeFullDto::getPatronymicName, EmployeeFullDto::setPatronymicName);
        binder.forField(dateOfBirth)
                .asRequired("Date of birth is required")
                .withValidator(value -> value.isBefore(LocalDate.now()), "Date of birth cannot be after now")
                .bind(EmployeeFullDto::getDateOfBirth, EmployeeFullDto::setDateOfBirth);
        binder.forField(phone)
                .asRequired("Phone is required")
                .withValidator(phone -> phone.matches("^(\\+7|8)[0-9]{10}$"),
                        "Phone number must be valid (e.g., +79301044124 or 89301044124)")
                .bind(EmployeeFullDto::getPhone, EmployeeFullDto::setPhone);
        binder.forField(email)
                .asRequired("Email is required")
                .bind(EmployeeFullDto::getEmail, EmployeeFullDto::setEmail);
        binder.forField(username)
                .asRequired("Username is required")
                .withValidator(value -> !value.trim().isEmpty(), "Username cannot be empty or spaces only")
                .bind(EmployeeFullDto::getUsername, EmployeeFullDto::setUsername);
        binder.forField(password)
                .asRequired("Password is required")
                .withValidator(value -> !value.trim().isEmpty(), "Password cannot be empty or spaces only")
                .bind(EmployeeFullDto::getPassword, EmployeeFullDto::setPassword);
        binder.forField(specializationsComboBox)
                .asRequired("Specialization is required")
                .bind(EmployeeFullDto::getSpecialization, EmployeeFullDto::setSpecialization);
        binder.bindInstanceFields(this);
    }

    private void setRequiredFields() {
        firstName.setRequiredIndicatorVisible(true);
        lastName.setRequiredIndicatorVisible(true);
        patronymicName.setRequiredIndicatorVisible(true);
        dateOfBirth.setRequiredIndicatorVisible(true);
        phone.setRequiredIndicatorVisible(true);
        email.setRequiredIndicatorVisible(true);
        username.setRequiredIndicatorVisible(true);
        password.setRequiredIndicatorVisible(true);
        specializationsComboBox.setRequiredIndicatorVisible(true);
    }

    private void configureGrid() {
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("patronymicName").setAutoWidth(true);
        grid.addColumn("dateOfBirth").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("username").setAutoWidth(true);
        grid.addColumn("specialization").setAutoWidth(true);
        grid.addColumn("projects").setAutoWidth(true);
        grid.setItems(query -> employeeService.getAll(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(EMPLOYEE_EDIT_ROUTE_TEMPLATE, event.getValue().getEmployeeId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(EditEmployeesView.class);
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
        firstName = new TextField("First name");
        lastName = new TextField("Last name");
        patronymicName = new TextField("Patronymic name");
        dateOfBirth = new DatePicker("Date of birth");
        phone = new TextField("Phone");
        email = new EmailField("Email");
        username = new TextField("Username");
        password = new PasswordField("Password");
        specializationsComboBox = new ComboBox<>("Specialization");
        specializationsComboBox.setWidth("min-content");
        setSpecializationsToComboBox();
        setRequiredFields();
        formLayout.add(firstName, lastName, patronymicName, dateOfBirth, phone, email, username, password, specializationsComboBox);

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
        specializationsComboBox.clear();
    }

    private void fillEditForm(EmployeeFullDto value) {
        this.employee = value;
        binder.readBean(this.employee);
    }

    private void setSpecializationsToComboBox() {
        List<String> specializations = specializationService.getAllSpecializationsNames();
        specializationsComboBox.setItems(specializations);
    }
}