package com.woosan.hr_system.vacation.service;

import com.woosan.hr_system.aspect.LogAfterExecution;
import com.woosan.hr_system.aspect.LogBeforeExecution;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.common.service.CommonService;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.vacation.dao.VacationDAO;
import com.woosan.hr_system.vacation.model.Vacation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    @Override // 모든 휴가 정보 조회
    public PageResult<Vacation> searchVacation(PageRequest pageRequest, String department, String status) {
        // 페이징을 위해 조회할 데이터의 시작위치 계산
        int offset = pageRequest.getPage() * pageRequest.getSize();
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", pageRequest.getKeyword());
        params.put("pageSize", pageRequest.getSize());
        params.put("offset", offset);
        params.put("department", department);
        params.put("status", status);

        // 휴가 정보 조회
        List<Vacation> vacationList = vacationDAO.searchVacation(params);
        int total = vacationList.size(); // 검색 결과 개수

        return new PageResult<>(vacationList, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    @Override // 해당 사원의 모든 휴가 정보 조회 (페이징)
    public PageResult<Vacation> getVacationByEmployeeId(PageRequest pageRequest, String employeeId) {
        // 페이징을 위해 조회할 데이터의 시작위치 계산
        int offset = pageRequest.getPage() * pageRequest.getSize();
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", pageRequest.getSize());
        params.put("offset", offset);
        params.put("employeeId", employeeId);

        List<Vacation> vacationList = vacationDAO.selectVacationByEmployeeId(params);  // 검색 결과 데이터
        int total = vacationList.size(); // 검색 결과 개수

        return new PageResult<>(vacationList, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    @Override // 해당 사원의 모든 휴가 정보 조회
    public List<Vacation> getVacationByEmployeeId(String employeeId) {
        return vacationDAO.getVacationByEmployeeId(employeeId);
    }

    @Override // 해당 부서의 모든 휴가 정보 조회
    public PageResult<Vacation> getVacationByDepartmentId(PageRequest pageRequest, String departmentId, String status) {
        // 해당 부서 사원 조회 후 아이디 리스트로 반환
        List<Employee> employeeList = employeeDAO.getEmployeesByDepartment(departmentId);
        List<String> employeeIdList = employeeList.stream()
                .map(Employee::getEmployeeId)
                .toList();

        // 페이징을 위해 조회할 데이터의 시작위치 계산
        int offset = pageRequest.getPage() * pageRequest.getSize();

        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", pageRequest.getSize());
        params.put("offset", offset);
        params.put("employeeIdList", employeeIdList);
        params.put("status", status);

        // 해당 부서 사원들 휴가 정보 조회
        List<Vacation> vacationList = vacationDAO.selectVacationByDepartmentId(params);

        int total = vacationList.size(); // 검색 결과 개수

        return new PageResult<>(vacationList, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    @Transactional
    @LogBeforeExecution
    @LogAfterExecution
    @Override // 휴가 신청
    public String requestVacation(Vacation vacation) {
        // 휴가 등록
        vacationDAO.insertVacation(vacation);

        // 알림 전송 후 메세지 반환
        String message = "'" + employeeDAO.getEmployeeName(vacation.getEmployeeId()) + "' 사원이 "
                + vacation.getVacationType()  + "를 신청하였습니다.";
        return vacation.getVacationType() + " 신청이 완료되었습니다.";
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

        // 휴가 승인 시 잔여 연차 확인
        float remainingLeave = employeeDAO.getEmployeeById(vacationInfo.getEmployeeId()).getRemainingLeave();
        if (vacationInfo.getApprovalStatus().equals("승인") && remainingLeave - vacationInfo.getUsedDays() < 0)
            throw new IllegalArgumentException("연차 사용 일수가 잔여 연차보다 많습니다.");

        // 휴가 객체 빌더패턴으로 새로 생성
        String processor = authService.getAuthenticatedUser().getNameWithId();
        Vacation updatedVacation = vacationInfo.toBuilder()
                .approvalStatus(status)
                .processingBy(processor)
                .processingAt(LocalDateTime.now())
                .build();
        vacationDAO.approveVacation(updatedVacation);

        // 알림 전송 후 메세지 반환
        String message = "휴가('" + vacationId + "')가 " + status + "되었습니다.";
        return message;
    }

    @Override // 휴가 삭제
    public String deleteVacation(int vacationId) {
        // 휴가 정보가 존재하는 지 확인
        findVacationById(vacationId);

        // 휴가 정보 삭제
        vacationDAO.deleteVacation(vacationId);

        // 알림 전송 후 메세지 반환
        String message = "휴가 정보('" + vacationId + "')가 삭제되었습니다.";
        return message;
    }

    @Override // 오늘 휴가인 사원 조회
    public List<Vacation> findEmployeesOnVacationToday() {
        return vacationDAO.getEmployeesOnVacationToday(LocalDate.now());
    }

}
