package com.woosan.hr_system.attendance.controller;

import com.woosan.hr_system.attendance.model.Attendance;
import com.woosan.hr_system.attendance.service.AttendanceService;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.vacation.model.Vacation;
import com.woosan.hr_system.vacation.service.VacationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.util.List;

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

    @GetMapping// 나의 근태 페이지
    public String viewMyAttendance(Model model) {
        // 로그인 사원 ID 조회
        String employeeId = authService.getAuthenticatedUser().getUsername();
        model.addAttribute("attendanceList", attendanceService.getAttendanceByEmployeeId(employeeId));
        return "attendance/my-log";
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
        model.addAttribute("attendance", attendanceService.hasTodayAttendanceRecord());
        return "attendance/commute";
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

}
