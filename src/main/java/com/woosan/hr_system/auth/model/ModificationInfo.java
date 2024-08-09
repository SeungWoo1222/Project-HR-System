package com.woosan.hr_system.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ModificationInfo {
    private String modifiedBy;
    private LocalDateTime lastModified;
}
