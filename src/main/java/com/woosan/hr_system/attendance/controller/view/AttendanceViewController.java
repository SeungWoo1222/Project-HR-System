package com.woosan.hr_system.attendance.controller.view;

import com.woosan.hr_system.attendance.model.Attendance;
import com.woosan.hr_system.attendance.service.AttendanceService;
import com.woosan.hr_system.attendance.service.OvertimeService;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.holiday.service.HolidayService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.vacation.model.Vacation;
import com.woosan.hr_system.vacation.service.VacationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/attendance")
public class AttendanceViewController {
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private AuthService authService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private VacationService vacationService;
    @Autowired
    private HolidayService holidayService;
    @Autowired
    private OvertimeService overtimeService;

    @GetMapping// 나의 근태 페이지
    public String viewMyAttendance(Model model) {
        // 로그인 사원 ID 조회 후 근태 기록 모델에 추가
        String employeeId = authService.getAuthenticatedUser().getUsername();
        model.addAttribute("attendanceList", attendanceService.getAttendanceByEmployeeId(employeeId));

        // 모든 공휴일 모델에 추가
        model.addAttribute("holidays", holidayService.getAllHoliday());

        return "attendance/my-attendance";
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getAttendanceSummary(@RequestParam("yearMonth") String yearMonthStr) {
        // 로그인 사원 아이디 조회
        String employeeId = authService.getAuthenticatedUser().getUsername();

        Map<String, Object> summary = new HashMap<>();

        // 사원 정보 조회 후 ID, 이름, 잔여 연차 맵에 담기
        Employee employee = employeeService.getEmployeeById(employeeId);
        summary.put("employeeId", employeeId);
        summary.put("name", employee.getName());
        summary.put("remainingLeave", employee.getRemainingLeave());

        YearMonth yearMonth = YearMonth.parse(yearMonthStr);

        // 근무 시간 맵에 담기
        Map<String, Object> thisMonthAttendance = attendanceService.getThisMonthAttendance(employeeId, yearMonth);
        double workingTime = (double) thisMonthAttendance.get("totalTime");
        int days = (int) thisMonthAttendance.get("days");
        summary.put("workingTime", workingTime);
        summary.put("days", days);

        // 초과근무, 야간근무 시간 맵에 담기
        Map<String, Object> thisMonthOvertimes = overtimeService.getThisMonthOvertimes(employeeId, yearMonth);
        double nightTime = (double) thisMonthOvertimes.get("nightTime");
        double totalOverTime = (double) thisMonthOvertimes.get("totalTime");
        summary.put("overtime", totalOverTime - nightTime);
        summary.put("nightTime", nightTime);

        summary.put("year", yearMonth.getYear());
        summary.put("month", yearMonth.getMonthValue());

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{attendanceId}") // 근태 상세 페이지
    public String viewAttendanceDetail(@PathVariable("attendanceId") int attendanceId, Model model) {
        // 근태 정보 상세 조회
        Attendance attendance = attendanceService.getAttendanceById(attendanceId);
        model.addAttribute(attendance);

        // 사원 정보 상세 조회 후 모델에 추가
        model.addAttribute("employee", employeeService.getEmployeeById(attendance.getEmployeeId()));
        return "attendance/detail";
    }

    @GetMapping("/commute") // 출퇴근 페이지
    public String viewCommuteModal(Model model) {
        // 로그인한 사원의 금일 근태기록 있는지 확인 후 모델에 추가
        Attendance attendance = attendanceService.hasTodayAttendanceRecord();
        model.addAttribute("attendance", attendance);

        String employeeId = authService.getAuthenticatedUser().getUsername();
        LocalDate today = LocalDate.now();

        // 이번 주 근무시간, 초과근무, 야간근무 시간 조회
        float totalWorkingTime = attendanceService.getTotalWeeklyWorkingTime(employeeId, today);
        float totalOverTime = overtimeService.getTotalWeeklyOvertime(employeeId, today);
        float totalNightTime = overtimeService.getTotalWeeklyNightOvertime(employeeId, today);

        // 조회한 총 시간들을 시간으로 변환
        int workingHours = convertToHours(totalWorkingTime);
        int overHours = convertToHours(totalOverTime);
        int overHoursWithoutNight = convertToHours(totalOverTime - totalNightTime);
        int nightHours = convertToHours(totalNightTime);
        int totalHours = convertToHours(totalWorkingTime + totalOverTime);

        // 모델에 추가
        model.addAttribute("workingHours", workingHours);
        model.addAttribute("workingMinutes", convertToMinutes(totalWorkingTime, workingHours));

        model.addAttribute("totalOverHours", overHours);
        model.addAttribute("totalOverMinutes", convertToMinutes(totalOverTime, overHours));

        model.addAttribute("overHours", overHoursWithoutNight);
        model.addAttribute("overMinutes", convertToMinutes((totalOverTime - totalNightTime), overHoursWithoutNight));

        model.addAttribute("nightHours", nightHours);
        model.addAttribute("nightMinutes", convertToMinutes(totalNightTime, nightHours));

        model.addAttribute("totalHours", totalHours);
        model.addAttribute("totalMinutes", convertToMinutes((totalWorkingTime + totalOverTime), totalHours));

        return "attendance/commute";
    }

    // 근무 시간을 시간과 분으로 분리
    private int convertToHours(float f) {
        return (int) f;
    }
    private int convertToMinutes(float f, int hours) {
        return (int) ((f - hours) * 60);
    }


    @GetMapping("/early-leave") // 조퇴 페이지
    public String viewEarlyLeaveModal(Model model) {
        // 로그인한 사원의 금일 근태기록 있는지 확인 후 모델에 추가
        model.addAttribute("attendance", attendanceService.hasTodayAttendanceRecord());
        return "attendance/early-leave";
    }

    @GetMapping("/{attendanceId}/edit") // 근태 수정 페이지
    public String viewEditAttendanceForm(@PathVariable("attendanceId") int attendanceId, Model model) {
        // 근태 정보 상세 조회
        Attendance attendance = attendanceService.getAttendanceById(attendanceId);
        model.addAttribute(attendance);

        String employeeId = attendance.getEmployeeId();

        // 사원 정보 상세 조회 후 모델에 추가
        model.addAttribute("employee", employeeService.getEmployeeById(employeeId));

        // 사원의 휴가 정보 조회 후 승인된 휴가 정보만 모델에 추가
        List<Vacation> vacationList = vacationService.getVacationByEmployeeId(employeeId).stream()
                .filter(vacation -> vacation.getApprovalStatus().equals("승인"))
                .toList();

        model.addAttribute("vacationList", vacationList);
        return "attendance/edit";
    }

    @GetMapping("/list") // 근태 목록 조회
    public String viewAttendanceList(@RequestParam(name = "page", defaultValue = "1") int page,
                                     @RequestParam(name = "size", defaultValue = "10") int size,
                                     @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                     @RequestParam(name = "department", defaultValue = "") String department,
                                     @RequestParam(name = "status", defaultValue = "") String status,
                                     @RequestParam(name = "yearmonth", defaultValue = "") String yearMonthString,
                                     Model model) {
        // 검색 년월 설정
        YearMonth yearMonth;
        if (yearMonthString.isEmpty()) {
            yearMonth = YearMonth.now();
        } else {
            yearMonth = YearMonth.parse(yearMonthString);
        }

        // 조건에 해당하는 검색 후 결과 페이징
        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Attendance> pageResult = attendanceService.searchAttendance(pageRequest, department, status, yearMonth);

        // 모델에 추가
        model.addAttribute("attendanceList", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("department", department);
        model.addAttribute("yearmonth", yearMonth);

        return "attendance/list";
    }

    @GetMapping("/list/{department}") // 부서 근태 목록 조회
    public String viewDepartmentAttendanceList(@RequestParam(name = "page", defaultValue = "1") int page,
                                               @RequestParam(name = "size", defaultValue = "10") int size,
                                               @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                               @PathVariable("department") String department,
                                               @RequestParam(name = "status", defaultValue = "") String status,
                                               @RequestParam(name = "yearmonth", defaultValue = "") String yearMonthString,
                                               Model model) {
        // 검색 년월 설정
        YearMonth yearMonth;
        if (yearMonthString.isEmpty()) {
            yearMonth = YearMonth.now();
        } else {
            yearMonth = YearMonth.parse(yearMonthString);
        }

        // 조건에 해당하는 검색 후 결과 페이징
        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Attendance> pageResult = attendanceService.searchAttendance(pageRequest, department, status, yearMonth);

        // 모델에 추가
        model.addAttribute("attendanceList", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("department", department);
        model.addAttribute("yearmonth", yearMonth);

        return "attendance/list-department";
    }

    @GetMapping("/today") // 오늘의 근태 현황 조회
    public String viewTodayAttendance(@RequestParam(name="normalPage", defaultValue = "1") int normalPage,
                                      @RequestParam(name="badPage", defaultValue = "1") int badPage,
                                      @RequestParam(name="tripPage", defaultValue = "1") int tripPage,
                                      Model model) {
        // 페이지 크기
        final int size = 5;

        // 오늘의 근태 현황 조회
        List<Attendance> attendanceList = attendanceService.getTodayAttendance();

        // 정상 출근 사원 리스트
        List<Attendance> normalAttendanceList = attendanceList.stream()
                .filter(attendance -> "출근".equals(attendance.getStatus()))
                .collect(Collectors.toList());

        // 근태 불량 사원 리스트
        List<Attendance> badAttendanceList = attendanceList.stream()
                .filter(attendance -> List.of("지각", "결근", "조퇴").contains(attendance.getStatus()))
                .collect(Collectors.toList());

        // 휴가 및 출장 사원 리스트
        List<Attendance> vacationOrBusinessList = attendanceList.stream()
                .filter(attendance -> List.of("휴가", "출장").contains(attendance.getStatus()))
                .collect(Collectors.toList());

        // 근태 상태별 갯수 세기
        Map<String, Long> statusCount = attendanceList.stream()
                .collect(Collectors.groupingBy(Attendance::getStatus, Collectors.counting()));

        // 출석률 계산
        int total = employeeService.getAllEmployee().size();
        Long rate = (long) ((((attendanceList.size())
                        - Objects.requireNonNullElse(statusCount.get("결근"), 0L)
                        - Objects.requireNonNullElse(statusCount.get("출장"), 0L)
                        - Objects.requireNonNullElse(statusCount.get("휴가"), 0L))
                        / (double) total) * 100);

        // 페이징 처리
        List<Attendance> pagedNormalAttendanceList = getPage(normalAttendanceList, normalPage, size);
        List<Attendance> pagedBadAttendanceList = getPage(badAttendanceList, badPage, size);
        List<Attendance> pagedVacationOrBusinessList = getPage(vacationOrBusinessList, tripPage, size);

        // 모델에 리스트 및 카운트 전달
        model.addAttribute("normalAttendanceList", pagedNormalAttendanceList);
        model.addAttribute("badAttendanceList", pagedBadAttendanceList);
        model.addAttribute("vacationOrBusinessList", pagedVacationOrBusinessList);
        model.addAttribute("statusCount", statusCount);
        model.addAttribute("total", total);
        model.addAttribute("rate", rate);

        // 현재 페이지와 총 페이지 수 전달
        model.addAttribute("normalCurrentPage", normalPage);
        model.addAttribute("normalTotalPages", (int) Math.ceil((double) normalAttendanceList.size() / size));

        model.addAttribute("badCurrentPage", badPage);
        model.addAttribute("badTotalPages", (int) Math.ceil((double) badAttendanceList.size() / size));

        model.addAttribute("tripCurrentPage", tripPage);
        model.addAttribute("tripTotalPages", (int) Math.ceil((double) vacationOrBusinessList.size() / size));

        return "attendance/today";
    }

    @GetMapping("/today-department") // 오늘의 부서원 근태 현황 조회
    public String viewTodayDepartmentAttendance(@RequestParam(name="normalPage", defaultValue = "1") int normalPage,
                                      @RequestParam(name="badPage", defaultValue = "1") int badPage,
                                      @RequestParam(name="tripPage", defaultValue = "1") int tripPage,
                                      Model model) {
        // 페이지 크기
        final int size = 5;

        // 로그인한 관리자의 부서
        String department = authService.getAuthenticatedUser().getDepartment();

        // 오늘의 근태 현황 조회
        List<Attendance> attendanceList = attendanceService.getTodayAttendance().stream()
                .filter(attendance -> {
                    String employeeDepartmentCode = attendance.getEmployeeId().substring(0, 2);
                    return employeeDepartmentCode.equals(department);
                })
                .toList();

        // 정상 출근 사원 리스트
        List<Attendance> normalAttendanceList = attendanceList.stream()
                .filter(attendance -> "출근".equals(attendance.getStatus()))
                .collect(Collectors.toList());

        // 근태 불량 사원 리스트
        List<Attendance> badAttendanceList = attendanceList.stream()
                .filter(attendance -> List.of("지각", "결근", "조퇴").contains(attendance.getStatus()))
                .collect(Collectors.toList());

        // 휴가 및 출장 사원 리스트
        List<Attendance> vacationOrBusinessList = attendanceList.stream()
                .filter(attendance -> List.of("휴가", "출장").contains(attendance.getStatus()))
                .collect(Collectors.toList());

        // 근태 상태별 갯수 세기
        Map<String, Long> statusCount = attendanceList.stream()
                .collect(Collectors.groupingBy(Attendance::getStatus, Collectors.counting()));

        // 출석률 계산
        int total = employeeService.getEmployeesByDepartment(department).size();
        Long rate = (long) ((((attendanceList.size())
                - Objects.requireNonNullElse(statusCount.get("결근"), 0L)
                - Objects.requireNonNullElse(statusCount.get("출장"), 0L)
                - Objects.requireNonNullElse(statusCount.get("휴가"), 0L))
                / (double) total) * 100);

        // 페이징 처리
        List<Attendance> pagedNormalAttendanceList = getPage(normalAttendanceList, normalPage, size);
        List<Attendance> pagedBadAttendanceList = getPage(badAttendanceList, badPage, size);
        List<Attendance> pagedVacationOrBusinessList = getPage(vacationOrBusinessList, tripPage, size);

        // 모델에 리스트 및 카운트 전달
        model.addAttribute("normalAttendanceList", pagedNormalAttendanceList);
        model.addAttribute("badAttendanceList", pagedBadAttendanceList);
        model.addAttribute("vacationOrBusinessList", pagedVacationOrBusinessList);
        model.addAttribute("statusCount", statusCount);
        model.addAttribute("total", total);
        model.addAttribute("rate", rate);

        // 현재 페이지와 총 페이지 수 전달
        model.addAttribute("normalCurrentPage", normalPage);
        model.addAttribute("normalTotalPages", (int) Math.ceil((double) normalAttendanceList.size() / size));

        model.addAttribute("badCurrentPage", badPage);
        model.addAttribute("badTotalPages", (int) Math.ceil((double) badAttendanceList.size() / size));

        model.addAttribute("tripCurrentPage", tripPage);
        model.addAttribute("tripTotalPages", (int) Math.ceil((double) vacationOrBusinessList.size() / size));

        return "attendance/today-department";
    }

    // 페이지 처리
    private List<Attendance> getPage(List<Attendance> list, int page, int size) {
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, list.size());

        if (fromIndex > list.size()) {
            return Collections.emptyList(); // 페이지가 리스트 크기를 초과하면 빈 리스트 반환
        }

        return list.subList(fromIndex, toIndex);
    }
}
