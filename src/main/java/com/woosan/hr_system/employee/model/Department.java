package com.woosan.hr_system.employee.model;

public enum Department {
    PR("생산"),
    QC("품질관리"),
    SA("영업"),
    MK("마케팅"),
    FI("재무"),
    HR("인사"),
    RD("연구개발");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }
}
