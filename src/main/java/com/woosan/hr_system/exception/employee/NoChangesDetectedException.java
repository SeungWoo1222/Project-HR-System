package com.woosan.hr_system.exception.employee;

public class NoChangesDetectedException extends RuntimeException{
    public NoChangesDetectedException() {
        super("변경된 사항이 없습니다.");
    }
}
