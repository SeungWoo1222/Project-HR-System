package com.woosan.hr_system.resignation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resignation {
    private String employeeId;
    private LocalDate resignationDate;
    private String resignationReason;
    private String codeNumber;
    private String specificReason;
    private String resignationDocuments;
    private String processedBy;
    private LocalDateTime processedDate;

    // 퇴사 정보를 초기화
    public void initializeResignationDetails(String employeeId, Resignation resignation, String processedBy, LocalDateTime processedDate) {
        this.employeeId = employeeId;
        this.resignationReason = classifyReason(resignation.getResignationReason());
        this.codeNumber = classifyCodeNumber(resignation.getCodeNumber());
        this.processedBy = processedBy;
        this.processedDate = processedDate;
    }

    // 퇴사 사유와 설명을 매핑하는 맵 초기화
    private static final Map<String, String> reasonDescriptions = new HashMap<>();
    static {
        reasonDescriptions.put("1", "1. 자진퇴사");
        reasonDescriptions.put("2", "2. 권고사직 : 회사 사정과 근로자 귀책에 의한 이직");
        reasonDescriptions.put("3", "3. 정년 등 기간만료에 의한 이직");
        reasonDescriptions.put("4", "4. 기타");
    }

    // 퇴사 코드와 설명을 매핑하는 맵 초기화
    private static final Map<String, String> codeDescriptions = new HashMap<>();
    static {
        codeDescriptions.put("11", "11. 개인사정으로 인한 자진퇴사");
        codeDescriptions.put("12", "12. 사업장 이전, 근로조건(계약조건) 변동, 임금체불 등으로 자진퇴사");
        codeDescriptions.put("22", "22. 폐업, 도산(예정 포함), 공사 중단");
        codeDescriptions.put("23", "23. 경영상 필요 및 회사 불황으로 인원 감축 등에 의한 퇴사 (해고•권고사직•계약파기 포함)");
        codeDescriptions.put("26", "26. 피보험자의 귀책사유에 의한 징계해고•권고사직 또는 계약 파기");
        codeDescriptions.put("31", "31. 정년");
        codeDescriptions.put("32", "32. 계약기간만료, 공사 종료");
        codeDescriptions.put("41", "41. 고용보험 비적용");
        codeDescriptions.put("42", "42. 이중고용");
    }

    // 퇴사 사유 분류하는 메소드
    private String classifyReason(String resignationReason) {
        String description = reasonDescriptions.get(resignationReason);
        if (description == null) {
            throw new IllegalArgumentException("잘못된 퇴사 사유입니다.");
        }
        return description;
    }

    // 퇴사 코드 분류하는 메소드
    private String classifyCodeNumber(String codeNumber) {
        String description = codeDescriptions.get(codeNumber);
        if (description == null) {
            throw new IllegalArgumentException("잘못된 퇴사 코드입니다.");
        }
        return description;
    }
}
