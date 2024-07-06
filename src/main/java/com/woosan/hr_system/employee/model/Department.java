package com.woosan.hr_system.employee.model;

public enum Department {
    PR("PR", "생산"),
    QC("QC", "품질관리"),
    SA("SA", "영업"),
    MK("MK", "마케팅"),
    FI("FI", "재무"),
    HR("HR", "인사"),
    RD("RD", "연구개발");

    private final String code;
    private final String displayName;

    Department(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return this.name() + "(" + this.displayName + ")";
    }

    public static Department fromCode(String code) {
        for (Department department : Department.values()) {
            if (department.getCode().equals(code)) {
                return department;
            }
        }
        throw new IllegalArgumentException("Invalid Department code: " + code);
    }
}
