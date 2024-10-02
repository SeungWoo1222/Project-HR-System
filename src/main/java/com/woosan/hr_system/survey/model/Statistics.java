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
    // 선택형 요소
    private List<Response> responses;
    private Map<String, Integer> responseCounts;

    // 입력형 요소
    private List<String[]> wordList;
}
