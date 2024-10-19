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
import ru.projects.model.Employee;
import ru.projects.model.dto.CreateEmployeeDto;
import ru.projects.model.dto.SpecializationDto;
import ru.projects.services.EmployeeService;
import ru.projects.services.SpecializationService;
import ru.projects.views.MainLayout;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 19.10.2024
 */
@PageTitle("Create Employee")
@Route(value = "create-employee", layout = MainLayout.class)
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
    private ComboBox<SpecializationDto> comboBox;


    public CreateEmployeeView(EmployeeService employeeService, SpecializationService specializationService) {
        this.employeeService = employeeService;
        this.specializationService = specializationService;
        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3();
        FormLayout formLayout2Col = new FormLayout();
        firstNameField = new TextField();
        lastNameField = new TextField();
        patronymicNameField = new TextField();
        dateOfBirth = new DatePicker();
        phoneNumberField = new TextField();
        loginField = new TextField();
        passwordField = new PasswordField();
        emailField = new EmailField();
        comboBox = new ComboBox<>();
        HorizontalLayout layoutRow = new HorizontalLayout();
        Button buttonSave = new Button();
        Button buttonCancel = new Button();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidth("1600px");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("1600px");
        h3.setText("Employee information");
        h3.setWidth("100%");
        formLayout2Col.setWidth("100%");
        firstNameField.setLabel("First Name");
        lastNameField.setLabel("Last Name");
        patronymicNameField.setLabel("Patronymic Name");
        dateOfBirth.setLabel("Birthday");
        phoneNumberField.setLabel("Phone Number");
        loginField.setLabel("Login");
        passwordField.setLabel("Password");
        passwordField.setWidth("min-content");
        emailField.setLabel("Email");
        comboBox.setLabel("Specialization");
        comboBox.setWidth("min-content");
        setComboBoxSampleData(comboBox);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("50px");
        layoutRow.setAlignItems(Alignment.CENTER);
        layoutRow.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonSave.setText("Save");
        buttonSave.setWidth("min-content");
        buttonSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonSave.addClickListener(buttonClickEvent -> saveEmployee());
        buttonCancel.setText("Cancel");
        buttonCancel.setWidth("min-content");
        buttonCancel.addClickListener(buttonClickEvent -> cancel());
        getContent().add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(firstNameField);
        formLayout2Col.add(lastNameField);
        formLayout2Col.add(patronymicNameField);
        formLayout2Col.add(dateOfBirth);
        formLayout2Col.add(phoneNumberField);
        formLayout2Col.add(loginField);
        formLayout2Col.add(passwordField);
        formLayout2Col.add(emailField);
        formLayout2Col.add(comboBox);
        layoutColumn2.add(layoutRow);
        layoutRow.add(buttonSave);
        layoutRow.add(buttonCancel);
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
        firstNameField.clear();
        lastNameField.clear();
        patronymicNameField.clear();
        dateOfBirth.clear();
        phoneNumberField.clear();
        loginField.clear();
        passwordField.clear();
        emailField.clear();
        comboBox.clear();
        Notification.show("The fields are cleared.");
    }

    private void setComboBoxSampleData(ComboBox comboBox) {
        List<SpecializationDto> specializations = specializationService.getAll();
        comboBox.setItems(specializations);
        comboBox.setItemLabelGenerator(specialization -> ((SpecializationDto) specialization).getSpecializationName());
    }
}
