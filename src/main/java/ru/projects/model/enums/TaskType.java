package ru.projects.model.enums;

import lombok.Getter;

@Getter
public enum TaskType {
    DEVELOPMENT("Development"),
    TESTING("Testing"),
    DEV_OPS("DevOps"),
    DATA_SCIENCE("Data Science"),
    DATA_ANALYSIS("Data Analysis");

    private final String displayName;

    TaskType(String displayName) {
        this.displayName = displayName;
    }

    public static TaskType fromDisplayName(String displayName) {
        for (TaskType taskType : TaskType.values()) {
            if (taskType.getDisplayName().equalsIgnoreCase(displayName)) {
                return taskType;
            }
        }
        throw new IllegalArgumentException("No task type found with displayName: " + displayName);
    }
}
