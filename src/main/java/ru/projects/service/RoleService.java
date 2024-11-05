package ru.projects.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.projects.model.Role;
import ru.projects.repository.RoleRepository;

import static ru.projects.util.Constants.AQA_ENGINEER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.BACKEND_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.DEVELOPER_ROLE_NAME;
import static ru.projects.util.Constants.FRONTEND_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.FULLSTACK_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.PROJECT_MANAGER_ROLE_NAME;
import static ru.projects.util.Constants.PROJECT_MANAGER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.QA_ENGINEER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.TESTER_ROLE_NAME;
import static ru.projects.util.Constants.USER_ROLE_NAME;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 01.11.2024
 */
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRoleBySpecializationName(String specializationName) {
        return switch (specializationName) {
            case FULLSTACK_DEVELOPER_SPECIALIZATION_NAME, BACKEND_DEVELOPER_SPECIALIZATION_NAME,
                 FRONTEND_DEVELOPER_SPECIALIZATION_NAME -> roleRepository.findByRoleName(DEVELOPER_ROLE_NAME);
            case QA_ENGINEER_SPECIALIZATION_NAME, AQA_ENGINEER_SPECIALIZATION_NAME -> roleRepository
                    .findByRoleName(TESTER_ROLE_NAME);
            case PROJECT_MANAGER_SPECIALIZATION_NAME -> roleRepository.findByRoleName(PROJECT_MANAGER_ROLE_NAME);
            default -> roleRepository.findByRoleName(USER_ROLE_NAME);
        };
    }
}
