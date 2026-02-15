package com.webapp.jobportal.enums;

public enum ApplicationStatus {
    PENDING("Pending"),
    REVIEWED("Reviewed"),
    SHORTLISTED("Shortlisted"),
    REJECTED("Rejected"),
    HIRED("Hired");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

