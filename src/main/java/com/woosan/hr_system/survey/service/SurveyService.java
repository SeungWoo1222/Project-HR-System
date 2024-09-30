package com.woosan.hr_system.survey.service;

import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.survey.model.Survey;

public interface SurveyService {
    Survey getSurveyById(int id);
    PageResult<Survey> searchSurvey(PageRequest pageRequest, String status);
    String submitSurvey(Survey survey);
    String updateSurvey(Survey survey);
}
