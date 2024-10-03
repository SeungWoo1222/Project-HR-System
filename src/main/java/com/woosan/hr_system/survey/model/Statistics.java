package com.woosan.hr_system.survey.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statistics { // 통계 정보 모델
    // 모든 응답
    private List<Response> responses;

    // 선택형 요소 및 시간 요소
    private Map<String, Integer> responseCounts;

    // 입력형 요소
    private List<Map<String, Object>> wordList;
}
