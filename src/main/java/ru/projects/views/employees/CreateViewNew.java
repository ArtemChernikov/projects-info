package ru.projects.views.employees;


import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import ru.projects.model.dto.CreateEmployeeDto;
import ru.projects.model.dto.SpecializationDto;
import ru.projects.services.EmployeeService;
import ru.projects.services.SpecializationService;
import ru.projects.views.MainLayout;

import java.time.LocalDate;
import java.util.List;

public class CreateViewNew extends Composite<VerticalLayout> {

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
    private ComboBox<SpecializationDto> comboBox;
    private FormLayout formLayout2Col;

    public CreateViewNew(EmployeeService employeeService, SpecializationService specializationService) {
        this.employeeService = employeeService;
        this.specializationService = specializationService;

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);

        initLayout();
        initFormFields();
        initButtons();
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
        comboBox = new ComboBox<>("Specialization");
        comboBox.setWidth("min-content");
        setComboBoxSampleData(comboBox);

        formLayout2Col.add(firstNameField, lastNameField, patronymicNameField, dateOfBirth,
                phoneNumberField, loginField, passwordField, emailField, comboBox);
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

        Button buttonCancel = new Button("Cancel", event -> cancel());
        buttonCancel.setWidth("min-content");

        layoutRow.add(buttonSave, buttonCancel);

        VerticalLayout layoutColumn2 = (VerticalLayout) getContent().getComponentAt(0);
        layoutColumn2.add(layoutRow);
    }

    private void saveEmployee() {
        String firstName = firstNameField.getValue();
        String lastName = lastNameField.getValue();
        String patronymicName = patronymicNameField.getValue();
        LocalDate birthday = dateOfBirth.getValue();
        String phoneNumber = phoneNumberField.getValue();
        String login = loginField.getValue();
        String password = passwordField.getValue();
        String email = emailField.getValue();
        String specializationName = comboBox.getValue().getSpecializationName();

        // Валидация данных (например, проверка на пустые поля)
        if (firstName.isEmpty() || lastName.isEmpty() || login.isEmpty()) {
            Notification.show("Please fill in all required fields.");
            return;
        }

        CreateEmployeeDto newEmployee = CreateEmployeeDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .patronymicName(patronymicName)
                .dateOfBirth(birthday)
                .phone(phoneNumber)
                .login(login)
                .password(password)
                .email(email)
                .specializationName(specializationName)
                .build();

        employeeService.save(newEmployee);

        Notification.show("Employee saved successfully.");
    }

    private void cancel() {
        clearFields();
        Notification.show("The fields are cleared.");
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        patronymicNameField.clear();
        dateOfBirth.clear();
        phoneNumberField.clear();
        loginField.clear();
        passwordField.clear();
        emailField.clear();
        comboBox.clear();
    }

    private void setComboBoxSampleData(ComboBox<SpecializationDto> comboBox) {
        List<SpecializationDto> specializations = specializationService.getAll();
        comboBox.setItems(specializations);
        comboBox.setItemLabelGenerator(SpecializationDto::getSpecializationName);
    }
}

