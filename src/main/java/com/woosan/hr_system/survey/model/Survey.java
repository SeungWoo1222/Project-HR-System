package com.woosan.hr_system.survey.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Survey {
    private int id;
    private String title;
    private String description;
    private List<Question> questions;
    private String createdBy;
    private LocalDateTime createdAt;
    private String status;
    private LocalDate expiresAt;
}
