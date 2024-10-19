package com.woosan.hr_system.schedule.model;

import com.fasterxml.jackson.annotation.JsonFormat;
//import com.woosan.hr_system.schedule.service.ValidScheduleDates;
import com.woosan.hr_system.schedule.service.ValidScheduleDates;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@ValidScheduleDates
public class Schedule {
    private int taskId;
    private String memberId;

    @NotBlank(message = "일정 제목을 입력해주세요.")
    private String taskName;

    @NotBlank(message = "일정 내용을 입력해주세요.")
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message ="시작일을 입력해주세요.")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message ="종료일을 입력해주세요.")
    private LocalDateTime endTime;

    private String status;
    private LocalDateTime createdDate;
    private Integer projectId;

    @NotNull(message = "하루종일 유무를 체크해주세요.")
    private boolean allDay;

    @NotBlank(message = "일정 색상을 선택해주세요.")
    private String color;
}
