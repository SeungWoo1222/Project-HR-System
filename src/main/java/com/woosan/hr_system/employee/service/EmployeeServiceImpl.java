package com.woosan.hr_system.employee.service;

import com.woosan.hr_system.aspect.LogAfterExecution;
import com.woosan.hr_system.aspect.LogBeforeExecution;
import com.woosan.hr_system.auth.model.Password;
import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.common.service.CommonService;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.model.Position;
import com.woosan.hr_system.exception.employee.EmployeeNotFoundException;
import com.woosan.hr_system.notification.service.NotificationService;
import com.woosan.hr_system.resignation.service.ResignationService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeDAO employeeDAO;
    @Autowired
    private AuthService authService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ResignationService resignationService;
    @Autowired
    private NotificationService notificationService;

    private static final int MAX_POSITION_RANK = 5;
    private static final String RESIGNED_STATUS = "퇴사";
    // ============================================ 조회 관련 로직 start-point ============================================
    @Override // id를 이용한 특정 사원 정보 조회
    public Employee getEmployeeById(String employeeId) {
        return findEmployeeById(employeeId);
    }

    @Override // id를 이용한 특정 사원의 이름 조회
    public String getEmployeeNameById(String employeeId) {
        Employee employee = findEmployeeById(employeeId);
        return employee.getName();
    }

    // 사원 정보를 찾는 메소드
    private Employee findEmployeeById(String employeeId) {
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        if (employee == null) {
            throw new EmployeeNotFoundException(employeeId);
        }
        return employee;
    }

    @Override // id를 이용한 특정 사원 정보 조회 (비밀번호 정보, 퇴사 정보 포함)
    public Employee getEmployeeDetails(String employeeId) {
        Employee employee = findEmployeeById(employeeId);
        // 비밀번호 정보 조회 및 설정
        verifyAndSetPasswordInfo(employee);
        // 퇴사 정보 조회 및 설정
        verifyAndSetResignationInfo(employee);
        return employee;
    }

    // 사원 비밀번호 정보 확인 후 설정하는 메소드
    private void verifyAndSetPasswordInfo(Employee employee) {
        String employeeId = employee.getEmployeeId();
        Password passwordInfo = authService.getPasswordInfoById(employeeId);
        employee.setPassword(passwordInfo);
    }

    // 사원 퇴사 정보 확인 후 설정하는 메소드
    private void verifyAndSetResignationInfo(Employee employee) {
        String employeeId = employee.getEmployeeId();
        if (employee.getStatus().equals(RESIGNED_STATUS)) {
            employee.setResignation(resignationService.getResignation(employeeId));
        }
    }

    @Override // 모든 사원 정보 조회
    public PageResult<Employee> searchEmployees(PageRequest pageRequest, String department) {
        // 페이징을 위해 조회할 데이터의 시작위치 계산
        int offset = pageRequest.getPage() * pageRequest.getSize();
        // 검색 결과 데이터
        List<Employee> employees = employeeDAO.searchEmployees(pageRequest.getKeyword(), pageRequest.getSize(), offset, department);
        // 검색 결과 총 개수
        int total = employeeDAO.countEmployees(pageRequest.getKeyword(), department);

        return new PageResult<>(employees, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    @Override // 퇴사 예정인 사원 정보 조회
    public List<Employee> getPreResignationEmployees() {
        return employeeDAO.getPreResignationEmployees();
    }

    @Override // 퇴사 후 2개월 이내의 사원 정보 조회
    public List<Employee> getResignedEmployees() {
        return employeeDAO.getResignedEmployees();
    }

    @Override // 퇴사 후 12개월이 지난 사원 정보 조회
    public List<Employee> getPreDeletionEmployees() {
        return employeeDAO.getPreDeletionEmployees();
    }

    @Override // 부서를 이용한 사원 리스트 조회
    public List<Employee> getEmployeesByDepartment(String departmentId) {
        return employeeDAO.getEmployeesByDepartment(departmentId);
    }

    @Override // 부서와 직급을 이용한 사원 조회
    public List<Employee> getEmployeesByDepartmentAndPosition(String department, String position) {
        Map<String, Object> map = new HashMap<>();
        map.put("department", department);
        map.put("position", position);
        return employeeDAO.selectEmployeesByDepartmentAndPosition(map);
    }

    @Override // 사원리스트 -> 사원 ID 리스트로 변경
    public List<String> convertEmployeesToIdList(List<Employee> employeeList) {
        return employeeList.stream()
                .map(Employee::getEmployeeId)
                .toList();
    }
    // ============================================= 조회 관련 로직 end-point =============================================

    // ============================================ 등록 관련 로직 start-point ============================================
    @LogBeforeExecution
    @LogAfterExecution
    @Transactional
    @Override // 사원 정보 등록
    public Map<String, Object> insertEmployee(Employee employee, MultipartFile picture) {
        // 업로드 후 사진 파일ID 할당
        if (picture == null || picture.isEmpty()) { throw new IllegalArgumentException("사원 사진이 업로드되지 않았습니다.\n사진을 업로드한 후 다시 시도해 주세요."); }
        assignPictureFromUpload(employee, picture);

        // 필수 필드를 검증
        verifyRegisterEmployeeFields(employee);

        // 사원 아이디 생성 후 중복 체크
        int year = employee.getHireDate().getYear();
        String employeeId = createEmployeeId(employee, year);
        verifyDuplicateId(employeeId);

        // 사원 아이디, 재직 상태, 기본 연차 설정
        employee.registerNewEmployee(employee, employeeId);

        // 사원 신규 등록
        employeeDAO.insertEmployee(employee);

        // 첫 비밀번호 생년월일로 설정
        authService.insertPassword(employeeId, employee.getBirth());

        // HR 차장에게 알림 전송 후 메세지 반환
        String message = "'" + employee.getName() + "' 사원이 신규 등록되었습니다.";
        sendNotificationToHRManager(message, "/employee/" + employee.getEmployeeId() + "/detail", "차장");

        // 메세지와 사원 아이디 반환
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("message", message);
        responseData.put("employeeId", employee.getEmployeeId());
        return responseData;
    }

    // 사원 등록 필수 필드 검증
    private void verifyRegisterEmployeeFields(Employee employee) {
        List<Object> requiredFields = Arrays.asList(
                employee.getName(),
                employee.getPhone(),
                employee.getEmail(),
                employee.getAddress(),
                employee.getDetailAddress(),
                employee.getDepartment(),
                employee.getPosition(),
                employee.getHireDate(),
                employee.getMaritalStatus(),
                employee.getNumDependents(),
                employee.getNumChildren()
        );
        boolean hasEmptyField = requiredFields.stream().anyMatch(Objects::isNull);
        if (hasEmptyField) {
            throw new IllegalArgumentException("입력하신 정보가 잘못되었습니다.");
        }
    }

    // 사원 아이디 생성
    private String createEmployeeId(Employee employee, int year) {
        return employee.createEmployeeId(employee, employeeDAO.countEmployeesByYear(year));
    }

    // 사원 아이디 중복 체크
    private void verifyDuplicateId(String employeeId) {
        if (employeeDAO.existsById(employeeId)) {
            throw new DuplicateKeyException("'" + employeeId + "'는 이미 존재하는 사원 아이디입니다.");
        }
    }
    // ============================================= 등록 관련 로직 end-point =============================================

    // ============================================ 수정 관련 로직 start-point ============================================
    @LogBeforeExecution
    @LogAfterExecution
    @Transactional
    @Override // 사원 정보 수정
    public String updateEmployee(Employee updatedEmployee, MultipartFile picture) {
        // 파일 체크 후 업로드 후 사진 파일ID 할당
        if (picture != null) {
            assignPictureFromUpload(updatedEmployee, picture);
        }

        // 변경사항 확인
        String employeeId = updatedEmployee.getEmployeeId();
        Employee originalEmployee = findEmployeeById(employeeId);
        checkForEmployeeChanges(originalEmployee, updatedEmployee);

        // 수정 사원번호와 수정 일시 설정
        setModificationInfoToEmployee(updatedEmployee);

        // 사원 정보 수정 처리
        employeeDAO.updateEmployee(updatedEmployee);

        return "'" + originalEmployee.getName() + "' 사원의 정보가 수정되었습니다.";
    }

    // Employee의 특정 필드만 비교하도록 필드 이름을 Set으로 전달하는 메소드
    private void checkForEmployeeChanges(Employee original, Employee updated) {
        Set<String> fieldsToCompare = new HashSet<>(Arrays.asList(
                "name", "birth", "residentRegistrationNumber", "phone", "email",
                "address", "detailAddress", "department", "position", "hireDate",
                "status", "remainingLeave", "picture", "maritalStatus", "numDependents", "numChildren"
        ));
        commonService.processFieldChanges(original, updated, fieldsToCompare);

        // 사진 변경 시 기존 파일 삭제
        int originalFileId = original.getPicture();
        if (originalFileId != updated.getPicture()) {
            fileService.deleteFile(originalFileId);
        }
    }

    // 사원 정보에 수정 정보 설정
    private void setModificationInfoToEmployee(Employee employee) {
        UserSessionInfo info = new UserSessionInfo();
        employee.setModifiedBy(info.getCurrentEmployeeId());
        employee.setLastModified(info.getNow());
    }

    @LogBeforeExecution
    @LogAfterExecution
    @Transactional
    @Override // 재직 상태 수정하는 메소드
    public String updateStatus(String employeeId, String status) {
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        // 수정 정보들 Map에 담기
        Map<String, Object> params = new HashMap<>();
        params.put("employeeId", employeeId);
        params.put("status", status);
        params.put("lastModified", userSessionInfo.getNow());
        params.put("modifiedBy", userSessionInfo.getCurrentEmployeeId());

        // 재직 상태 수정
        employeeDAO.updateStatus(params);

        // HR 부장에게 알림 전송 후 메세지 반환
        String message = "'" + getEmployeeNameById(employeeId) + "' 사원의 재직 상태가 '" + status + "'으로 변경되었습니다.";
        sendNotificationToHRManager(message, "/employee/" + employeeId + "/detail", "부장");
        return message;
    }

    @LogBeforeExecution
    @LogAfterExecution
    @Transactional
    @Override // 직급 승진시키는 메소드
    public String promoteEmployee(String employeeId) {
        // 현재 직급 rank 가져온 뒤 부장과 사장인지 확인
        int positionRank = Position.getRankByPositionName(employeeDAO.getEmployeeById(employeeId).getPosition().name());
        if (positionRank >= MAX_POSITION_RANK) throw new IllegalArgumentException("해당 직급에서는 더 이상 승진할 수 없습니다.");

        // 직급 +1
        Position positionToBePromoted = Position.fromRank(positionRank + 1);

        UserSessionInfo userSessionInfo = new UserSessionInfo();
        // 사원 번호와 승진할 직급 Map에 담기
        Map<String, Object> params = new HashMap<>();
        params.put("employeeId", employeeId);
        params.put("positionId", positionToBePromoted);
        params.put("lastModified", userSessionInfo.getNow());
        params.put("modifiedBy", userSessionInfo.getCurrentEmployeeId());

        // 사원 승진 처리
        employeeDAO.updatePosition(params);

        // HR 부장에게 알림 전송 후 메세지 반환
        String message = "'" + getEmployeeNameById(employeeId) + "' 사원이 '" + positionToBePromoted + "'으로 승진하였습니다.";
        sendNotificationToHRManager(message, "/employee/" + employeeId + "/detail", "부장");
        return message;
    }
    // ============================================ 수정 관련 로직 end-point ============================================

    // ============================================== 삭제 로직 start-point ==============================================
    @LogBeforeExecution
    @LogAfterExecution
    @Transactional
    @Override // 사원 정보 삭제
    public String deleteEmployee(String employeeId) {
        Employee employee = findEmployeeById(employeeId);

        // 삭제 전 퇴사 정보 확인
        verifyAndSetResignationInfo(employee);

        LocalDate resignationDate = employee.getResignation().getResignationDate(); // 퇴사일자
        LocalDate oneYearLater = resignationDate.plusDays(365); // 퇴사일자 1년 경과일
        LocalDate now = LocalDate.now();

        if (oneYearLater.isBefore(now) || oneYearLater.isEqual(now)) {
            resignationService.deleteResignation(employeeId); // 퇴사 정보 삭제
            fileService.deleteFile(employee.getPicture()); // 사원 사진 삭제
            authService.deletePassword(employeeId); // 사원 비밀번호 정보 삭제
            notificationService.removeAllNotification(employeeId); // 사원 모든 알림 삭제
            employeeDAO.deleteEmployee(employeeId); // 사원 정보 삭제

            // HR 부장에게 알림 전송 후 메세지 반환
            String message = "'" + employee.getName() + "' 사원의 정보가 삭제되었습니다.";
            sendNotificationToHRManager(message, null, "부장");
            return message;
        } else {
            throw new IllegalArgumentException("사원이 퇴사 후 1년이 지나지 않았습니다.");
        }
    }
    // =============================================== 삭제 로직 end-point ===============================================

    // ============================================== 기타 로직 start-point ==============================================
    // 업로드 후 사진 파일 ID 할당
    private void assignPictureFromUpload(Employee employee, MultipartFile picture) {
        int fileId = fileService.uploadingFile(picture, "employee");
        employee.assignPicture(fileId);
    }

    // 인사과(HR) 관리자에게 알림 전송
    private void sendNotificationToHRManager(String message, String url, String position) {
        // HR 관리자 검색하여 ID 리스트로 변환
        List<String> managerIdList = convertEmployeesToIdList(getEmployeesByDepartmentAndPosition("HR", position));
        // 알림 전송
        notificationService.createNotifications(managerIdList, message + "<br>처리자 : " + authService.getAuthenticatedUser().getNameWithId(), url);
    }
}
