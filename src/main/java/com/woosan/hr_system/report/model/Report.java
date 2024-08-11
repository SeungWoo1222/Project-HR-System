package com.woosan.hr_system.report.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Report {
    private int reportId;
    private String writerId;
    private String approverId;
    private String approverName;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String status;
    private String rejectReason;
    private LocalDate completeDate;

    // 보고서 작성 시 결재자 선택에서 여러 임원을 선택하기 위한 변수
    private List<String> nameList;
    private List<String> idList;

    // 결재할 보고서에 작성자 이름을 표기하기 위한 변수
    private String writerName;

}
