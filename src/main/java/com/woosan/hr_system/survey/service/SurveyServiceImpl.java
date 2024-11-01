package com.woosan.hr_system.survey.service;

import com.woosan.hr_system.aspect.LogAfterExecution;
import com.woosan.hr_system.aspect.LogBeforeExecution;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.notification.service.NotificationService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.survey.dao.SurveyDAO;
import com.woosan.hr_system.survey.model.*;
import com.woosan.hr_system.utils.KiwiSingleton;
import kr.pe.bab2min.Kiwi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private KiwiSingleton kiwiSingleton;

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

        int surveyId = responses.get(0).getSurveyId();
        Survey survey = getSurveyById(surveyId);

        // 조사가 종료된 설문인지 확인
        if (survey.getStatus().equals("조사 종료")) {
            throw new IllegalArgumentException("조사가 종료된 설문입니다.");
        }

        // 사원이 설문에 참여했는지 조회
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

        // 설문 조사자에게 알림 전송
        String creator = survey.getCreatedBy();
        int startIndex = creator.indexOf('(') + 1;
        int endIndex = creator.indexOf(')');
        String creatorId = creator.substring(startIndex, endIndex);
        notificationService.createNotification(creatorId,
                authService.getAuthenticatedUser().getNameWithId() + "가 설문에 참여하였습니다.",
                "/survey/participants?surveyId=" + surveyId);

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

    @Override // 모든 응답이 포함된 설문 조회
    public Survey getSurveyWithAllResponse(int surveyId) {
        // 설문 조회
        Survey survey = surveyDAO.selectSurveyById(surveyId);
        List<Question> questions = survey.getQuestions();

        // 질문과 응답 매칭
        List<Question> questionsWithResponses = questions.stream()
                .peek(question -> {
                    // 질문에 해당하는 모든 응답 조회 후 담기
                    int questionId = question.getId();
                    List<Response> responses = surveyDAO.selectResponses(surveyId, questionId);
                    Statistics statistics = new Statistics();
                    statistics.setResponses(responses);

                    // 질문 타입이 단일 선택(radio) 또는 다중 선택(checkbox)인 경우
                    if (question.getQuestionType().equals("radio") || question.getQuestionType().equals("checkbox")) {
                        // 질문의 각 옵션에 대한 응답 수 처리
                        Map<String, Integer> optionCounts = processOptionCounts(question, responses);

                        // 통계 모델에 옵션에 대한 응답 수 추가
                        statistics.setResponseCounts(optionCounts);

                    // 질문 타입이 단답형(text) 또는 장문형(textarea)인 경우
                    } else if (question.getQuestionType().equals("text") || question.getQuestionType().equals("textarea")) {
                        // 질문의 각 답변 형태소 분석 처리
                        List<Map<String, Object>> wordList = processTextResponses(responses);
                        // 통계 모델에 단어 구름으로 출력할 단어 리스트 추가
                        statistics.setWordList(wordList);
                    // 나머지 날짜(date) 또는 시간(time)인 경우
                    } else {
                        Map<String, Integer> answerCounts = new HashMap<>();

                        for (Response response : responses) {
                            String answer = response.getAnswer();
                            // 각 응답에 대한 카운트
                            answerCounts.put(answer, answerCounts.getOrDefault(answer, 0) + 1);
                        }

                        // 통계 모델에 응답에 대한 카운트 수 추가
                        statistics.setResponseCounts(answerCounts);
                    }
                    // 통계 데이터 질문에 담기
                    question.setStatistics(statistics);
                })
                .toList();

        // 응답이 포함된 설문 반환
        return survey.toBuilder()
                .questions(questionsWithResponses)
                .build();
    }

    // 질문의 각 옵션에 대한 응답 수 처리
    private Map<String, Integer> processOptionCounts(Question question, List<Response> responses) {
        Map<String, Integer> optionCounts = new HashMap<>();
        List<String> options = question.getOptions();

        // 각 선택지에 대해 카운트 0으로 설정
        for (String option : options) {
            optionCounts.put(option, 0);
        }

        // 각 응답에 대해 선택된 값 카운트
        for (Response response : responses) {
            String selectedOption = response.getAnswer();
            optionCounts.put(selectedOption, optionCounts.get(selectedOption) + 1);
        }
        return optionCounts;
    }

    // 질문의 각 텍스트 kiwi 형태소 분석기 이용하여 응답 형태소 분석 후 단어 구름으로 출력할 단어 리스트 처리
    private List<Map<String, Object>> processTextResponses(List<Response> responses) {
        // kiwi 라이브러리를 이용하여 자연어 처리
        Kiwi kiwi = kiwiSingleton.getKiwi();

        Map<String, Integer> wordCounts = new HashMap<>();

        // 각 응답을 형태소 분석 후
        for (Response response : responses) {
            String answer = response.getAnswer();
            // 형태소 분석
            Kiwi.Token[] tokens = kiwi.tokenize(answer, Kiwi.Match.allWithNormalizing);
            for (Kiwi.Token token : tokens) {
                switch (token.tag) { // token.tag 타입이 byte
                    case 1: // 일반 명사, NNG, 1
                    case 2: // 고유 명사, NNP, 2
                        wordCounts.put(token.form, wordCounts.getOrDefault(token.form, 0) + 1);
                        break;
                    case 5: // 형용사, VA, 5
                        String word = token.form + "다"; // 형용사는 ~다 형태로
                        wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
                        break;
                }
            }
        }

        // JQCloud 출력을 위한 Map을 List<Map<String, Object>>로 변환
        List<Map<String, Object>> wordList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
            Map<String, Object> word = new HashMap<>();
            word.put("text", entry.getKey()); // 키를 첫 번째 요소로
            word.put("weight", entry.getValue()); // 값을 두 번째 요소로 (String으로 변환)
            wordList.add(word);
        }

        // 리스트 내림차순 정렬
        wordList.sort((map1, map2) -> {
            Integer weight1 = (Integer) map1.get("weight");
            Integer weight2 = (Integer) map2.get("weight");
            return weight2.compareTo(weight1);
        });

        return wordList;
    }

    // 설문조사 만료일이 지나면 설문 상태 변경
    @Scheduled(cron = "0 33 23 * * ?") // 매일 자정 00:00에 실행
    public void updateSurveyStatusIfExpired() {
        // 조사 중인 설문 조회
        List<Survey> surveys = surveyDAO.selectInvestigatingSurvey();

        // 만료일이 지난 설문 조사 종료 처리
        surveys.stream()
                .filter(survey -> survey.getExpiresAt().isBefore(LocalDate.now()))
                .forEach(expiredSurvey -> {
                    surveyDAO.closeSurvey(expiredSurvey.getId());
                });
    }
}