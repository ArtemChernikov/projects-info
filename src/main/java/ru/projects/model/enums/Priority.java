package ru.projects.model.enums;

import lombok.Getter;

@Getter
public enum Priority {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low");

    private final String displayName;

    Priority(String displayName) {
        this.displayName = displayName;
    }

    public static Priority fromDisplayName(String displayName) {
        for (Priority priority : Priority.values()) {
            if (priority.getDisplayName().equalsIgnoreCase(displayName)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("No priority found with displayName: " + displayName);
    }
}
