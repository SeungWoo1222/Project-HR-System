package com.woosan.hr_system.vacation.service;

import com.woosan.hr_system.aspect.LogAfterExecution;
import com.woosan.hr_system.aspect.LogBeforeExecution;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.common.service.CommonService;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.vacation.dao.VacationDAO;
import com.woosan.hr_system.vacation.model.Vacation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class VacationServiceImpl implements VacationService {
    @Autowired
    private VacationDAO vacationDAO;
    @Autowired
    private EmployeeDAO employeeDAO;
    @Autowired
    private CommonService commonService;
    @Autowired
    private AuthService authService;

    @Override // 아이디를 이용한 휴가 정보 조회
    public Vacation getVacationById(int vacationId) {
        return findVacationById(vacationId);
    }

    // 아이디를 이용한 휴가 정보 조회
    private Vacation findVacationById(int vacationId) {
        Vacation vacationInfo = vacationDAO.selectVacationById(vacationId);
        if (vacationInfo == null) {
            throw new IllegalArgumentException("해당 휴가 정보를 찾을 수 없습니다.\n휴가 ID : " + vacationId);
        }
        return vacationInfo;
    }

    @Override // 해당 사원의 모든 휴가 정보 조회
    public List<Vacation> getVacationByEmployeeId(String employeeId) {
        return vacationDAO.selectVacationByEmployeeId(employeeId);
    }

    @Override // 해당 부서의 모든 휴가 정보 조회
    public List<Vacation> getVacationByDepartmentId(String departmentId) {
        return vacationDAO.selectVacationByDepartmentId(departmentId);
    }

    @Transactional
    @LogBeforeExecution
    @LogAfterExecution
    @Override // 휴가 신청
    public String requestVacation(Vacation vacation) {
        // 휴가 등록
        vacationDAO.createVacation(vacation);

        // 알림 전송 후 메세지 반환
        String message = "'" + employeeDAO.getEmployeeName(vacation.getEmployeeId()) + "' 사원이 "
                + vacation.getVacationType()  + "를 신청하였습니다.";
        return message;
    }

    @Transactional
    @LogBeforeExecution
    @LogAfterExecution
    @Override // 휴가 수정
    public String editVacation(Vacation vacation) {
        // 이미 처리된 휴가를 휴가자 본인이 수정 시도 시 안내메세지 출력
        if (!vacation.getApprovalStatus().equals("미처리") &&
                vacation.getEmployeeId().equals(authService.getAuthenticatedUser().getUsername()))
            throw new IllegalArgumentException("이미 처리된 휴가는 수정할 수 없습니다.\n변경사항이 있다면 인사 부서에 문의해주세요.");

        // 기존 휴가 정보 조회 후 비교
        int vacationId = vacation.getVacationId();
        Vacation originalVacation = findVacationById(vacationId);
        checkForVacationChanges(originalVacation, vacation);

        // 휴가 수정
        vacationDAO.updateVacation(vacation);
        return "휴가 정보('" + vacationId  + "')가 수정되었습니다.";
    }

    // Vacation의 특정 필드만 비교하도록 필드 이름을 Set으로 전달하는 메소드
    private void checkForVacationChanges(Vacation original, Vacation updated) {
        Set<String> fieldsToCompare = new HashSet<>(Arrays.asList(
            "startAt", "endAt", "vacationType", "reason", "usedDays"
        ));
        commonService.processFieldChanges(original, updated, fieldsToCompare);
    }

    @Transactional
    @LogBeforeExecution
    @LogAfterExecution
    @Override // 휴가 처리
    public String processVacation(int vacationId, String status) {
        // 해당 휴가 조회 및 미처리 휴가인지 검사
        Vacation vacationInfo = findVacationById(vacationId);
        if (!vacationInfo.getApprovalStatus().equals("미처리")) throw new IllegalArgumentException("이미 처리된 휴가입니다.");

        // 휴가 객체 빌더패턴으로 새로 생성
        String processor = authService.getAuthenticatedUser().getNameWithId();
        Vacation updatedVacation = Vacation.builder()
                .approvalStatus(status)
                .processingBy(processor)
                .processingAt(LocalDateTime.now())
                .build();
        vacationDAO.approveVacation(updatedVacation);

        // 알림 전송 후 메세지 반환
        String message = "휴가('" + vacationId + "')가 " + status + "되었습니다.";
        return message;
    }
}
