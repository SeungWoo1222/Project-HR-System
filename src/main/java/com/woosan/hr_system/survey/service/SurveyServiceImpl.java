package com.woosan.hr_system.survey.service;

import com.woosan.hr_system.aspect.LogAfterExecution;
import com.woosan.hr_system.aspect.LogBeforeExecution;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.survey.dao.SurveyDAO;
import com.woosan.hr_system.survey.model.Participant;
import com.woosan.hr_system.survey.model.Question;
import com.woosan.hr_system.survey.model.Response;
import com.woosan.hr_system.survey.model.Survey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SurveyServiceImpl implements SurveyService {
    @Autowired
    private SurveyDAO surveyDAO;
    @Autowired
    private AuthService authService;

    @Override // ID를 이용한 설문 조회
    public Survey getSurveyById(int id) {
        Survey survey = surveyDAO.selectSurveyById(id);
        if (survey == null) {
            throw new IllegalArgumentException("해당 설문조사가 존재하지 않습니다.\n설문 ID : " + id);
        }
        return survey;
    }

    @Override // 설문 검색 조회
    public PageResult<Survey> searchSurvey(PageRequest pageRequest, String status) {
        // 페이징을 위해 조회할 데이터의 시작위치 계산
        int offset = pageRequest.getPage() * pageRequest.getSize();
        // 검색 결과 데이터
        List<Survey> surveys = surveyDAO.searchSurvey(pageRequest.getKeyword(), pageRequest.getSize(), offset, status);
        // 검색 결과 총 개수
        int total = surveys.size();

        return new PageResult<>(surveys, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    @LogBeforeExecution
    @LogAfterExecution
    @Transactional
    @Override // 새로운 설문 등록
    public String submitSurvey(Survey survey) {
        // 설문 정보 부분 등록
        int surveyId = addSurvey(survey);

        // 질문 부분 등록
        addQuestion(survey.getQuestions(), surveyId);

        return "새로운 설문조사가 등록되었습니다.";
    }

    @Override // 설문 삭제
    public String deleteSurvey(int id) {
        // 설문 조회
        Survey survey = surveyDAO.selectSurveyById(id);
        if (survey == null) {
            throw new IllegalArgumentException("해당 설문이 존재하지 않습니다.\n설문 ID : " + id);
        }

        // 작성자 본인인지 확인
        String employeeId = authService.getAuthenticatedUser().getUsername();
        if (!extractEmployeeId(survey.getCreatedBy()).equals(employeeId)) {
            throw new IllegalArgumentException("설문 작성자 본인만 삭제할 수 있습니다.");
        }

        // 설문 삭제
        surveyDAO.deleteSurvey(id);
        return "설문('" + id + "')이 삭제되었습니다.";
    }

    // 설문 정보 등록
    private int addSurvey(Survey survey) {
        Survey newSurvey = Survey.builder()
                .title(survey.getTitle())
                .description(survey.getDescription())
                .createdBy(authService.getAuthenticatedUser().getNameWithId())
                .createdAt(LocalDateTime.now())
                .expiresAt(survey.getExpiresAt())
                .build();
        return surveyDAO.insertSurvey(newSurvey);
    }

    // 질문 등록
    private void addQuestion(List<Question> questions, int surveyId) {
        for (Question question : questions) {
            Question newQuestion = Question.builder()
                    .surveyId(surveyId)
                    .questionText(question.getQuestionText())
                    .questionType(question.getQuestionType())
                    .build();
            int questionId = surveyDAO.insertQuestion(newQuestion);
            if (!question.getOptions().isEmpty()) {
                addOption(question.getOptions(), questionId);
            }
        }
    }

    // 질문 타입이 radio 또는 checkbox 경우 옵션 등록
    private void addOption(List<String> options, int questionId) {
        for (String option : options) {
            Map<String, Object> map = new HashMap<>();
            map.put("questionId", questionId);
            map.put("optionText", option);
            surveyDAO.insertOption(map);
        }
    }

    @Transactional
    @Override // 설문 응답 제출
    public String submitResponse(List<Response> responses) {
        // 로그인한 사원 ID 조회
        String employeeId = authService.getAuthenticatedUser().getUsername();

        // 사원이 설문에 참여했는지 조회
        int surveyId = responses.get(0).getSurveyId();
        List<String> participantIds = getParticipantIds(surveyId);
        boolean isAlreadyParticipated = participantIds.stream()
                .anyMatch(participantId -> participantId.equals(employeeId));
        if (isAlreadyParticipated) {
            throw new IllegalArgumentException("이미 설문에 참여하셨습니다.");
        }

        // 응답 등록
        for (Response response : responses) {
            // 사원 ID 추가하여 객체 생성 후 등록
            Response newResponse = response.toBuilder()
                    .employeeId(employeeId)
                    .build();
            surveyDAO.insertResponse(newResponse);
        }

        // 설문 참여자 등록
        surveyDAO.insertParticipant(employeeId, surveyId);

        return "응답이 성공적으로 제출되었습니다.\n감사합니다!";
    }

    @Override // 설문 참여자 조회
    public List<String> getParticipantIds(int surveyId) {
        return surveyDAO.selectParticipantIds(surveyId);
    }

    @Override // 설문 응답 참여자 검색 조회
    public PageResult<Participant> searchParticipants(PageRequest pageRequest, int surveyId) {
        // 페이징을 위해 조회할 데이터의 시작위치 계산
        int offset = pageRequest.getPage() * pageRequest.getSize();
        // 검색 결과 데이터
        List<Participant> participants = surveyDAO.searchParticipants(pageRequest.getSize(), offset, surveyId);
        // 검색 결과 총 개수
        int total = participants.size();

        return new PageResult<>(participants, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    @Override // 응답이 포함된 설문 조회
    public Survey getSurveyWithResponse(int surveyId, String employeeId) {
        // 설문 조회
        Survey survey = surveyDAO.selectSurveyById(surveyId);
        List<Question> questions = survey.getQuestions();

        // 설문 응답 조회
        List<Response> responses = surveyDAO.selectResponses(surveyId, employeeId);

        // 질문과 응답을 questionId로 매칭
        List<Question> questionsWithResponses = questions.stream()
                .peek(question -> responses.stream()
                        .filter(response -> response.getQuestionId() == question.getId())
                        .findFirst()
                        .ifPresent(question::setResponse))
                .toList();

        // 응답이 포함된 설문 반환
        return survey.toBuilder()
                .questions(questionsWithResponses)
                .build();
    }

    @Override // 설문 참여자 정보 조회
    public Participant getParticipantInfo(int surveyId, String employeeId) {
        return surveyDAO.selectParticipantInfo(surveyId, employeeId);
    }

    @Override // 이름(사원ID)에서 사원 ID 추출하는 함수
    public String extractEmployeeId(String fullNameWithId) {
        String[] parts = fullNameWithId.split("\\(");
        return parts[1].replace(")", "");
    }
}

