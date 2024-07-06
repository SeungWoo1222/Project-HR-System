package com.woosan.hr_system.employee.model;

public enum Position {
    EMPLOYEE(1, "사원"),
    ASSISTANT_MANAGER(2, "대리"),
    MANAGER(3, "과장"),
    DEPUTY_GENERAL_MANAGER(4, "차장"),
    GENERAL_MANAGER(5, "부장"),
    PRESIDENT(6, "사장");

    private final int code;
    private final String displayName;

    Position(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return this.name() + "(" + this.displayName + ")";
    }

    public static Position fromCode(int code) {
        for (Position position : Position.values()) {
            if (position.getCode() == code) {
                return position;
            }
        }
        throw new IllegalArgumentException("Invalid Position code: " + code);
    }
}
