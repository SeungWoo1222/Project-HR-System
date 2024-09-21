package com.woosan.hr_system.attendance.service;

import com.woosan.hr_system.attendance.dao.AttendanceDAO;
import com.woosan.hr_system.attendance.model.Attendance;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.common.service.CommonService;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.holiday.service.HolidayService;
import com.woosan.hr_system.schedule.service.BusinessTripService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.vacation.model.Vacation;
import com.woosan.hr_system.vacation.service.VacationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

@Slf4j
@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    private AuthService authService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private AttendanceDAO attendanceDAO;
    @Autowired
    private HolidayService holidayService;
    @Autowired
    private VacationService vacationService;
    @Autowired
    private BusinessTripService businessTripService;
    @Autowired
    private EmployeeService employeeService;

    @Override // 근태 정보 조회
    public Attendance getAttendanceById(int attendanceId) {
        return findAttendanceById(attendanceId);
    }

    // 근태 ID를 통한 근태 상세 조회
    private Attendance findAttendanceById(int attendanceId) {
        Attendance attendance = attendanceDAO.getAttendanceById(attendanceId);
        if (attendance == null) {
            throw new IllegalArgumentException("해당 근태 정보를 찾을 수 없습니다.\n 근태 ID : " + attendanceId);
        }
        return attendance;
    }

    @Override // 사원 ID를 통한 해당 사원의 근태 목록 조회
    public List<Attendance> getAttendanceByEmployeeId(String employeeId) {
        return attendanceDAO.getAttendanceByEmployeeId(employeeId);
    }

    @Override // 모든 근태 목록 조회
    public List<Attendance> getAllAttendance() {
        return attendanceDAO.getAllAttendance();
    }

    @Override // 금일 근태 현황 조회
    public List<Attendance> getTodayAttendance() {
        return attendanceDAO.getTodayAttendance();
    }

    @Override // 근태 목록 검색 조회
    public PageResult<Attendance> searchAttendance(PageRequest pageRequest, String department, String status, YearMonth yearMonth) {
        int offset = pageRequest.getPage() * pageRequest.getSize();
        log.info("offset : " + offset);
        log.info("size : " + pageRequest.getSize());

        // 검색 조건들 map에 넣어 전달
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", pageRequest.getKeyword());
        params.put("pageSize", pageRequest.getSize());
        params.put("offset", offset);
        params.put("department", department);
        params.put("status", status);
        params.put("yearMonth", yearMonth);

        List<Attendance> attendanceList = attendanceDAO.searchAttendance(params);
        int total = attendanceList.size();

        return new PageResult<>(attendanceList, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    @Override // 로그인한 사원의 금일 근태기록 있는지 확인
    public Attendance hasTodayAttendanceRecord () {
        // 로그인 사원 ID 조회
        String employeeId = authService.getAuthenticatedUser().getUsername();

        // 오늘의 근태 현황 조회
        List<Attendance> todayAttendanceList = getTodayAttendance();

        // Optional 클래스 이용하여 로그인 사원의 금일 근태 기록이 있는지 확인
        Optional<Attendance> optionalAttendance = todayAttendanceList.stream()
                .filter(attendance -> attendance.getEmployeeId().equals(employeeId))
                .findFirst();

        if (optionalAttendance.isPresent()) { // 값이 있다면 사용
            return optionalAttendance.get();
        } else { // 값이 없다면 null 처리
            return null;
        }
    }

    @Override // 출근
    public String checkIn() {
        // 현재 로그인된 사원 조회
        String employeeId = authService.getAuthenticatedUser().getUsername();

        // 시간에 따른 근태 상태 설정
        LocalTime now = LocalTime.now();
        LocalTime nineAM = LocalTime.of(9, 0);  // 오전 9시
        String status;

        if (now.isBefore(nineAM)) {
            status = "출근";  // 오전 9시 이전이면 출근 상태
        } else {
            status = "지각";  // 오전 9시 이후면 지각 상태
        }

        // 근태 객체 생성
        Attendance attendance = Attendance.builder()
                .employeeId(employeeId)
                .date(LocalDate.now())
                .checkIn(LocalTime.now())
                .status(status)
                .build();

        // 출근 처리
        attendanceDAO.insertAttendance(attendance);

        LocalDate today = LocalDate.now();
        return today.getYear() + "년 " + today.getMonthValue() + "월 " + today.getDayOfMonth() + "일 출근 체크가 완료되었습니다."
                + "\n출근 시간은 " + now.getHour() + "시" + now.getMinute() + "분입니다."
                + "\n오늘도 좋은 하루 되세요!";
    }

    @Override // 퇴근
    public String checkOut() {
        // 금일 근태 ID 조회
        int todayAttendanceId = getMyTodayAttendance();

        LocalTime now = LocalTime.now();

        Map<String, Object> params = new HashMap<>();
        params.put("todayAttendanceId", todayAttendanceId);
        params.put("checkOut", now);

        // 퇴근 처리
        attendanceDAO.updateCheckout(params);

        LocalDate today = LocalDate.now();
        return today.getYear() + "년 " + today.getMonthValue() + "월 " + today.getDayOfMonth() + "일 퇴근 체크가 완료되었습니다."
                + "\n퇴근 시간은 " + now.getHour() + "시" + now.getMinute() + "분입니다."
                + "\n오늘도 고생 많으셨습니다!";
    }

    @Override // 조퇴
    public String earlyLeave(String notes) {
        // 금일 근태 ID 조회
        int todayAttendanceId = getMyTodayAttendance();

        LocalTime now = LocalTime.now();

        Map<String, Object> params = new HashMap<>();
        params.put("todayAttendanceId", todayAttendanceId);
        params.put("checkOut", now);
        params.put("status", "조퇴");
        params.put("notes", notes);

        // 조퇴 처리
        attendanceDAO.updateEarlyLeave(params);

        LocalDate today = LocalDate.now();
        return today.getYear() + "년 " + today.getMonthValue() + "월 " + today.getDayOfMonth() + "일 조퇴 체크가 완료되었습니다."
                + "\n조퇴 시간은 " + now.getHour() + "시" + now.getMinute() + "분입니다."
                + "\n오늘도 고생 많으셨습니다!";
    }

    // 로그인된 사원의 금일 근태 ID 조회
    private int getMyTodayAttendance() {
        // 현재 로그인된 사원 조회
        String employeeId = authService.getAuthenticatedUser().getUsername();

        Map<String, Object> params = new HashMap<>();
        params.put("employeeId", employeeId);
        params.put("date", LocalDate.now());

        // 근태 ID 조회 후 반환
        return attendanceDAO.getMyTodayAttendance(params);
    }

    @Override // 근태 수정
    public String editAttendance(Attendance attendance) {
        // 기존 근태 정보 조회
        Attendance originalAttendance = getAttendanceById(attendance.getAttendanceId());

        // 변경사항 확인
        checkForAttendanceChanges(originalAttendance, attendance);

        // 마지막 수정 일시와 마지막 수정 사원 ID 세팅하여 새로운 객체 생성
        Attendance updatedAttendance = attendance.toBuilder()
                .lastModified(LocalDateTime.now())
                .modifiedBy(authService.getAuthenticatedUser().getNameWithId())
                .build();

        // 근태 정보 수정
        attendanceDAO.updateAttendance(updatedAttendance);

        return "근태 정보('" + attendance.getAttendanceId()  + "')가 수정되었습니다.";
    }

    // Attendance의 특정 필드만 비교하도록 필드 이름을 Set으로 전달하는 메소드
    private void checkForAttendanceChanges(Attendance original, Attendance updated) {
        Set<String> fieldsToCompare = new HashSet<>(Arrays.asList(
                "checkIn", "checkOut", "status", "vacationId", "tripId", "notes"
        ));
        commonService.processFieldChanges(original, updated, fieldsToCompare);
    }

    // 휴가, 출장 사원 근태 자동 등록
    // 평일 오전 9시에 실행, 공휴일은 제외
    @Transactional
    @Scheduled(cron = "0 0 9 * * MON-FRI")
    public void registerAttendance() {
        LocalDate today = LocalDate.now();

        // 오늘이 공휴일이면 작업 중단
        if (holidayService.isHoliday(today)) {
            log.info("금일은 공휴일입니다. 근태 자동 등록 작업을 중단합니다.");
            return;
        }

        // 휴가 사원 근태 등록
        registerVacationAttendance();

        // 출장 사원 근태 등록 - 출장 관련 기능 미구현으로 주석 처리
//        registerTripAttendance();

        log.info("{}년 {}월 {}일 휴가 및 출장 사원의 근태가 등록되었습니다.", today.getYear(), today.getMonth(), today.getDayOfMonth());
    }

    // 휴가 사원 근태 등록
    private void registerVacationAttendance() {
        // 휴가 사원 목록 조회
        List<Vacation> employeeListOnVacation = vacationService.findEmployeesOnVacationToday();

        // 객체 생성하여 근태 등록
        for (Vacation vacation : employeeListOnVacation) {
            // 근태 객체 생성
            Attendance attendance = Attendance.builder()
                    .employeeId(vacation.getEmployeeId())
                    .date(LocalDate.now())
                    .checkIn(LocalTime.of(0, 0, 0))
                    .checkOut(LocalTime.of(0, 0, 0))
                    .status("휴가")
                    .vacationId(vacation.getVacationId())
                    .build();
            // 근태 등록
            attendanceDAO.insertAttendance(attendance);
        }
    }

    // 출장 사원 근태 등록 - 출장 관련 기능 미구현으로 주석 처리
    private void registerTripAttendance() {
//        // 출장 사원 목록 조회
//        List<BusinessTrip> employeeListOnTrip = businessTripService.findEmployeesOnTripToday();
//
//        // 객체 생성하여 근태 등록
//        for (BusinessTrip trip : employeeListOnTrip) {
//            // 근태 객체 생성
//            Attendance attendance = Attendance.builder()
//                    .employeeId(trip.getEmployeeId())
//                    .date(LocalDate.now())
//                    .checkIn(LocalTime.of(9, 0, 0))
//                    .checkOut(LocalTime.of(18, 0, 0))
//                    .status("출장")
//                    .vacationId(trip.getTripId())
//                    .build();
//            // 근태 등록
//            attendanceDAO.insertAttendance(attendance);
//        }
    }

    // 결근 자동 처리
    // 평일 오후 6시에 실행, 공휴일은 제외
    @Transactional
    @Scheduled(cron = "0 0 18 * * MON-FRI")
    public void registerAbsence() {
        LocalDate today = LocalDate.now();

        // 오늘이 공휴일이면 작업 중단
        if (holidayService.isHoliday(today)) {
            log.info("금일은 공휴일입니다. 결근 자동 처리 작업을 중단합니다.");
            return;
        }

        // 모든 사원 조회 후 재직중인 사원 ID 필터
        List<String> workingEmployeeIdList = employeeService.getAllEmployee().stream()
                .filter(employee -> employee.getStatus().equals("재직") || employee.getStatus().equals("퇴사 예정"))
                .map(Employee::getEmployeeId)
                .toList();

        // 금일 근태 현황 조회 후 기록이 있는 사원의 ID 리스트 필터
        List<String> presentEmployeeIdList = attendanceDAO.getTodayAttendance().stream()
                .map(Attendance::getEmployeeId)
                .toList();

        // 재직중인 사원 ID 리스트와 기록이 있는 사원 ID 리스트 비교하여 결근한 사원 ID 리스트 추출
        List<String> absentEmployeeIdList = workingEmployeeIdList.stream()
                .filter(employeeId -> !presentEmployeeIdList.contains(employeeId))  // 기록에 사원이 없는 경우 필터
                .toList();

        // 객체 생성하여 근태 등록
        for (String employeeId : absentEmployeeIdList) {
            // 근태 객체 생성
            Attendance attendance = Attendance.builder()
                    .employeeId(employeeId)
                    .date(LocalDate.now())
                    .checkIn(LocalTime.of(0, 0, 0))
                    .checkOut(LocalTime.of(0, 0, 0))
                    .status("결근")
                    .build();
            // 근태 등록
            attendanceDAO.insertAttendance(attendance);
        }

        log.info("{}년 {}월 {}일 결근한 사원의 근태가 등록되었습니다.", today.getYear(), today.getMonth(), today.getDayOfMonth());
    }
}
