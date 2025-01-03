package ru.projects.view.employees;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import ru.projects.model.dto.employee.EmployeeDto;
import ru.projects.service.EmployeeService;
import ru.projects.service.SpecializationService;
import ru.projects.view.MainLayout;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 19.10.2024
 */
@PageTitle("Create Employee")
@Route(value = "create-employee", layout = MainLayout.class)
@RolesAllowed(value = {"ROLE_ADMIN"})
@Menu(order = 3, icon = "line-awesome/svg/user-plus-solid.svg")
@Slf4j
public class CreateEmployeeView extends Composite<VerticalLayout> {

    private final EmployeeService employeeService;
    private final SpecializationService specializationService;
    private TextField firstName;
    private TextField lastName;
    private TextField patronymicName;
    private DatePicker dateOfBirth;
    private TextField phoneNumber;
    private TextField username;
    private PasswordField password;
    private EmailField email;
    private ComboBox<String> specialization;
    private FormLayout formLayout2Col;
    private BeanValidationBinder<EmployeeDto> binder;

    private EmployeeDto employeeDto;

    public CreateEmployeeView(EmployeeService employeeService, SpecializationService specializationService) {
        this.employeeService = employeeService;
        this.specializationService = specializationService;

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

    private void saveEmployee() {
        log.info("VIEW: Saving employee.");
        try {
            if (this.employeeDto == null) {
                this.employeeDto = new EmployeeDto();
            }
            binder.writeBean(this.employeeDto);
            employeeService.save(employeeDto);
            clearForm();
            log.info("VIEW: Employee saved.");
            Notification.show("Employee saved successfully.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            log.error("VIEW: Failed to create employee: {}", e.getMessage());
            Notification.show("Failed to create employee. Check again that all values are valid",
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

        Button buttonSave = new Button("Save", event -> saveEmployee());
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

        H3 h3 = new H3("Employee Information");
        h3.setWidth("100%");

        formLayout2Col = new FormLayout();
        formLayout2Col.setWidth("100%");

        layoutColumn2.add(h3, formLayout2Col);

        getContent().add(layoutColumn2);
    }

    private void initFormFields() {
        firstName = new TextField("First name");
        lastName = new TextField("Last name");
        patronymicName = new TextField("Patronymic name");
        dateOfBirth = new DatePicker("Birthday");
        phoneNumber = new TextField("Phone number");
        username = new TextField("Username");
        password = new PasswordField("Password");
        password.setWidth("min-content");
        email = new EmailField("Email");
        specialization = new ComboBox<>("Specialization");
        specialization.setWidth("min-content");
        setSpecializationsToComboBox();

        formLayout2Col.add(firstName, lastName, patronymicName, dateOfBirth,
                phoneNumber, username, password, email, specialization);
    }

    private void configureValidationBinder() {
        binder = new BeanValidationBinder<>(EmployeeDto.class);
        binder.forField(firstName)
                .asRequired("First name is required")
                .withValidator(value -> !value.trim().isEmpty(), "First name cannot be empty or spaces only")
                .withValidator(value -> value.matches("^[a-zA-Zа-яА-ЯёЁ]+$"), "First name must contain only letters")
                .bind(EmployeeDto::getFirstName, EmployeeDto::setFirstName);
        binder.forField(lastName)
                .asRequired("Last name is required")
                .withValidator(value -> !value.trim().isEmpty(), "Last name cannot be empty or spaces only")
                .withValidator(value -> value.matches("^[a-zA-Zа-яА-ЯёЁ]+$"), "Last name must contain only letters")
                .bind(EmployeeDto::getLastName, EmployeeDto::setLastName);
        binder.forField(patronymicName)
                .asRequired("Patronymic name is required")
                .withValidator(value -> !value.trim().isEmpty(), "Patronymic name cannot be empty or spaces only")
                .withValidator(value -> value.matches("^[a-zA-Zа-яА-ЯёЁ]+$"), "Patronymic name must contain only letters")
                .bind(EmployeeDto::getPatronymicName, EmployeeDto::setPatronymicName);
        binder.forField(dateOfBirth)
                .asRequired("Date of birth is required")
                .withValidator(value -> value.isBefore(LocalDate.now()), "Date of birth cannot be after now")
                .bind(EmployeeDto::getDateOfBirth, EmployeeDto::setDateOfBirth);
        binder.forField(phoneNumber)
                .asRequired("Phone is required")
                .withValidator(phone -> phone.matches("^(\\+7|8)[0-9]{10}$"),
                        "Phone number must be valid (e.g., +79301044124 or 89301044124)")
                .bind(EmployeeDto::getPhone, EmployeeDto::setPhone);
        binder.forField(email)
                .asRequired("Email is required")
                .bind(EmployeeDto::getEmail, EmployeeDto::setEmail);
        binder.forField(username)
                .asRequired("Username is required")
                .withValidator(value -> !value.trim().isEmpty(), "Username cannot be empty or spaces only")
                .bind(EmployeeDto::getUsername, EmployeeDto::setUsername);
        binder.forField(password)
                .asRequired("Password is required")
                .withValidator(value -> !value.trim().isEmpty(), "Password cannot be empty or spaces only")
                .bind(EmployeeDto::getPassword, EmployeeDto::setPassword);
        binder.forField(specialization)
                .asRequired("Specialization is required")
                .bind(EmployeeDto::getSpecialization, EmployeeDto::setSpecialization);
        binder.bindInstanceFields(this);
    }

    private void setRequiredFields() {
        firstName.setRequiredIndicatorVisible(true);
        lastName.setRequiredIndicatorVisible(true);
        phoneNumber.setRequiredIndicatorVisible(true);
        dateOfBirth.setRequiredIndicatorVisible(true);
        phoneNumber.setRequiredIndicatorVisible(true);
        email.setRequiredIndicatorVisible(true);
        username.setRequiredIndicatorVisible(true);
        password.setRequiredIndicatorVisible(true);
        specialization.setRequiredIndicatorVisible(true);
    }

    private void clearForm() {
        this.employeeDto = null;
        binder.readBean(this.employeeDto);
        specialization.clear();
    }

    private void setSpecializationsToComboBox() {
        List<String> specializations = specializationService.getAllSpecializationsNames();
        specialization.setItems(specializations);
    }
}
