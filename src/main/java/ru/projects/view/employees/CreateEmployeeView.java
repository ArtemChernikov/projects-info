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
import ru.projects.model.dto.EmployeeDto;
import ru.projects.service.EmployeeService;
import ru.projects.service.SpecializationService;
import ru.projects.view.MainLayout;

import java.util.List;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 19.10.2024
 */
@PageTitle("Create Employee")
@Route(value = "create-employee", layout = MainLayout.class)
@RolesAllowed(value = {"ROLE_ADMIN"})
@Menu(order = 3, icon = "line-awesome/svg/user.svg")
public class CreateEmployeeView extends Composite<VerticalLayout> {

    private final EmployeeService employeeService;
    private final SpecializationService specializationService;
    private TextField firstNameField;
    private TextField lastNameField;
    private TextField patronymicNameField;
    private DatePicker dateOfBirth;
    private TextField phoneNumberField;
    private TextField loginField;
    private PasswordField passwordField;
    private EmailField emailField;
    private ComboBox<String> specializationsComboBox;
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
        try {
            if (this.employeeDto == null) {
                this.employeeDto = new EmployeeDto();
            }
            binder.writeBean(this.employeeDto);
            employeeService.save(employeeDto);
            clearForm();
            Notification.show("Employee saved successfully.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
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
        firstNameField = new TextField("First Name");
        lastNameField = new TextField("Last Name");
        patronymicNameField = new TextField("Patronymic Name");
        dateOfBirth = new DatePicker("Birthday");
        phoneNumberField = new TextField("Phone Number");
        loginField = new TextField("Login");
        passwordField = new PasswordField("Password");
        passwordField.setWidth("min-content");
        emailField = new EmailField("Email");
        specializationsComboBox = new ComboBox<>("Specialization");
        specializationsComboBox.setWidth("min-content");
        setSpecializationsToComboBox();

        formLayout2Col.add(firstNameField, lastNameField, patronymicNameField, dateOfBirth,
                phoneNumberField, loginField, passwordField, emailField, specializationsComboBox);
    }

    private void configureValidationBinder() {
        binder = new BeanValidationBinder<>(EmployeeDto.class);
        binder.forField(firstNameField)
                .asRequired("First Name is required")
                .bind(EmployeeDto::getFirstName, EmployeeDto::setFirstName);
        binder.forField(lastNameField)
                .asRequired("Last Name is required")
                .bind(EmployeeDto::getLastName, EmployeeDto::setLastName);
        binder.forField(patronymicNameField)
                .asRequired("Patronymic Name is required")
                .bind(EmployeeDto::getPatronymicName, EmployeeDto::setPatronymicName);
        binder.forField(dateOfBirth)
                .asRequired("Date of Birth is required")
                .bind(EmployeeDto::getDateOfBirth, EmployeeDto::setDateOfBirth);
        binder.forField(phoneNumberField)
                .asRequired("Phone is required")
                .bind(EmployeeDto::getPhone, EmployeeDto::setPhone);
        binder.forField(emailField)
                .asRequired("Email is required")
                .bind(EmployeeDto::getEmail, EmployeeDto::setEmail);
        binder.forField(loginField)
                .asRequired("Login is required")
                .bind(EmployeeDto::getLogin, EmployeeDto::setLogin);
        binder.forField(passwordField)
                .asRequired("Password is required")
                .bind(EmployeeDto::getPassword, EmployeeDto::setPassword);
        binder.forField(specializationsComboBox)
                .asRequired("Specialization is required")
                .bind(EmployeeDto::getSpecialization, EmployeeDto::setSpecialization);
        binder.bindInstanceFields(this);
    }

    private void setRequiredFields() {
        firstNameField.setRequiredIndicatorVisible(true);
        lastNameField.setRequiredIndicatorVisible(true);
        phoneNumberField.setRequiredIndicatorVisible(true);
        dateOfBirth.setRequiredIndicatorVisible(true);
        phoneNumberField.setRequiredIndicatorVisible(true);
        emailField.setRequiredIndicatorVisible(true);
        loginField.setRequiredIndicatorVisible(true);
        passwordField.setRequiredIndicatorVisible(true);
        specializationsComboBox.setRequiredIndicatorVisible(true);
    }

    private void clearForm() {
        this.employeeDto = null;
        binder.readBean(this.employeeDto);
        specializationsComboBox.clear();
    }

    private void setSpecializationsToComboBox() {
        List<String> specializations = specializationService.getAllSpecializationsNames();
        specializationsComboBox.setItems(specializations);
    }
}
