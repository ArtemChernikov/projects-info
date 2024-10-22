package ru.projects.model.enums;

import lombok.Getter;

@Getter
public enum Status {
    NEW("New"),
    IN_PROGRESS("In Progress"),
    FINISHED("Finished");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public static Status fromDisplayName(String displayName) {
        for (Status status : Status.values()) {
            if (status.getDisplayName().equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No status found with displayName: " + displayName);
    }
}
