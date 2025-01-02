package ru.projects.model.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 19.10.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class EmployeeDto {

    @NotEmpty(message = "First name cannot be empty")
    @Pattern(
            regexp = "^[a-zA-Zа-яА-ЯёЁ]+$",
            message = "First name must contain only letters"
    )
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    @Pattern(
            regexp = "^[a-zA-Zа-яА-ЯёЁ]+$",
            message = "Last name must contain only letters"
    )
    private String lastName;

    @NotEmpty(message = "Patronymic name cannot be empty")
    @Pattern(
            regexp = "^[a-zA-Zа-яА-ЯёЁ]+$",
            message = "Patronymic name must contain only letters"
    )
    private String patronymicName;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotEmpty(message = "Phone cannot be empty")
    @Pattern(
            regexp = "^(\\+7|8)[0-9]{10}$",
            message = "Phone number must be valid (e.g., +79301044324 or 89301044324)"
    )
    private String phone;

    @Email(message = "Email not valid")
    private String email;

    @NotEmpty(message = "Username cannot be empty")
    private String username;

    @NotEmpty(message = "Password cannot be empty")
    private String password;

    @NotEmpty(message = "Specialization cannot be empty")
    private String specialization;
}
