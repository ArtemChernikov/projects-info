package ru.projects.model.enums;

import lombok.Getter;

import static ru.projects.util.Constants.AQA_ENGINEER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.BACKEND_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.DATA_ANALYST_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.DATA_SCIENTIST_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.DEV_OPS_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.FRONTEND_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.FULLSTACK_DEVELOPER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.PROJECT_MANAGER_SPECIALIZATION_NAME;
import static ru.projects.util.Constants.QA_ENGINEER_SPECIALIZATION_NAME;

@Getter
public enum EnumSpecialization {
    PROJECT_MANAGER(PROJECT_MANAGER_SPECIALIZATION_NAME),
    BACKEND_DEVELOPER(BACKEND_DEVELOPER_SPECIALIZATION_NAME),
    FRONTEND_DEVELOPER(FRONTEND_DEVELOPER_SPECIALIZATION_NAME),
    FULLSTACK_DEVELOPER(FULLSTACK_DEVELOPER_SPECIALIZATION_NAME),
    QA_ENGINEER(QA_ENGINEER_SPECIALIZATION_NAME),
    AQA_ENGINEER(AQA_ENGINEER_SPECIALIZATION_NAME),
    DEV_OPS(DEV_OPS_SPECIALIZATION_NAME),
    DATA_SCIENTIST(DATA_SCIENTIST_SPECIALIZATION_NAME),
    DATA_ANALYST(DATA_ANALYST_SPECIALIZATION_NAME);

    private final String specializationName;

    EnumSpecialization(String specializationName) {
        this.specializationName = specializationName;
    }

    public static EnumSpecialization fromSpecializationName(String specializationName) {
        for (EnumSpecialization enumSpecialization : EnumSpecialization.values()) {
            if (enumSpecialization.getSpecializationName().equalsIgnoreCase(specializationName)) {
                return enumSpecialization;
            }
        }
        throw new IllegalArgumentException("No specialization found with specializationName: " + specializationName);
    }

}