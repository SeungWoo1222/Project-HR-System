//package com.woosan.hr_system.survey.service;
//
//import com.woosan.hr_system.search.PageRequest;
//import com.woosan.hr_system.search.PageResult;
//import com.woosan.hr_system.survey.model.Participant;
//import com.woosan.hr_system.survey.model.Response;
//import com.woosan.hr_system.survey.model.Survey;
//
//import java.util.List;
//
//public interface SurveyService {
//    Survey getSurveyById(int id);
//    List<Survey> getAllSurvey();
//    PageResult<Survey> searchSurvey(PageRequest pageRequest, String status);
//    String submitSurvey(Survey survey);
//    String deleteSurvey(int id);
//    String submitResponse(List<Response> responses);
//    List<String> getParticipantIds(int surveyId);
//    PageResult<Participant> searchParticipants(PageRequest pageRequest, int surveyId);
//    Survey getSurveyWithResponse(int surveyId, String employeeId);
//    Participant getParticipantInfo(int surveyId, String employeeId);
//    String extractEmployeeId(String fullNameWithId);
//    Survey getSurveyWithAllResponse(int surveyId);
//}
