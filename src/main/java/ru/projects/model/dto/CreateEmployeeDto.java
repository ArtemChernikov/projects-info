package ru.projects.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Builder
public class CreateEmployeeDto {

    private String specializationName;

    private String firstName;

    private String lastName;

    private String patronymicName;

    private LocalDate dateOfBirth;

    private String phone;

    private String email;

    private String login;

    private String password;
}
