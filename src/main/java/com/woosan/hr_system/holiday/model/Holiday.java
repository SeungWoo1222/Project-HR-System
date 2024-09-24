package com.woosan.hr_system.holiday.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Holiday {
    private int holidayId;
    private String dateName;
    private LocalDate locDate;

    public Holiday (String name, LocalDate date) {
        this.dateName = name;
        this.locDate = date;
    }
}
