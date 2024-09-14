package com.woosan.hr_system.schedule.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    private int contactId;
    private String clientName;
    private String contactNumber;
    private String contactEmail;
    private String note;
}
