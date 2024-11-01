package com.woosan.hr_system.report.model;

import com.woosan.hr_system.report.service.validation.ValidDueDate;
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
@ValidDueDate
public class Request {
    private int requestId;
    private Integer reportId;
    private String requesterId;
    private String writerId;
    private String writerName;
    private LocalDateTime requestDate;
    private LocalDateTime modifiedDate;
    // 내게 온 요청 목록 조회 시
    private String requesterName;

    @NotNull(message ="마감일을 입력해주세요.")
    private LocalDate dueDate;

    @NotBlank(message="요청 내용을 입력해주세요.")
    private String requestNote;

    // 요청 작성 시 작성자 선택에서 여러 임원을 선택하기 위한 변수
    @Valid
    @Size(min = 1, message = "작성자를 선택해주세요.")
    private List<@NotBlank(message = "작성자를 선택해주세요.") String> nameList;

    @Valid
    @Size(min = 1, message = "작성자를 선택해주세요.")
    private List<@NotBlank(message = "작성자를 선택해주세요.") String> idList;


}
