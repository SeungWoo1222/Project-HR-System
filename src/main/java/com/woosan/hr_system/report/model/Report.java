package com.woosan.hr_system.report.model;

import com.woosan.hr_system.report.service.validation.ValidCompleteDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidCompleteDate
public class Report {
    private int reportId;
    private String writerId;
    private String approverId;
    private String approverName;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String status;
    private String rejectReason;
    // 결재할 보고서에 작성자 이름을 표기하기 위한 변수
    private String writerName;

    @NotBlank(message="보고서 제목을 입력해주세요.")
    private String title;

    @NotBlank(message="보고서 내용을 입력해주세요.")
    private String content;

    @NotNull(message ="완료 날짜를 입력해주세요.")
    private LocalDate completeDate;

    // 보고서 작성 시 결재자 선택에서 여러 임원을 선택하기 위한 변수
    @Valid
    @Size(min = 1, message = "결재자를 선택해주세요.")
    private List<@NotBlank(message = "결재자를 선택해주세요.") String> nameList;
    @Valid
    @Size(min = 1, message = "결재자를 선택해주세요.")
    private List<@NotBlank(message="결재자를 선택해주세요.") String> idList;


}
