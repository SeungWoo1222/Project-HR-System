package com.woosan.hr_system.common;

import com.woosan.hr_system.attendance.service.AttendanceService;
import com.woosan.hr_system.attendance.service.OvertimeService;
import com.woosan.hr_system.auth.model.Password;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.file.service.FileService;
import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.report.service.RequestService;
import com.woosan.hr_system.schedule.model.Schedule;
import com.woosan.hr_system.schedule.service.ScheduleService;
import com.woosan.hr_system.survey.service.SurveyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping
public class CommonController {
    @Autowired
    private AuthService authService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private FileService fileService;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private OvertimeService overtimeService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private SurveyService surveyService;

    @GetMapping("home") // 홈 화면으로 이동
    public String home(Model model) {
        String employeeId = authService.getAuthenticatedUser().getUsername();
        Employee employee = employeeService.getEmployeeById(employeeId);

        // 사원 사진
        model.addAttribute("pictureUrl", fileService.getUrl(employee.getPicture()));

        // 이번 주 근무시간
        LocalDate today = LocalDate.now();

        float totalWorkingTime = attendanceService.getTotalWeeklyWorkingTime(employeeId, today);
        float totalOverTime = overtimeService.getTotalWeeklyOvertime(employeeId, today);
        float totalNightTime = overtimeService.getTotalWeeklyNightOvertime(employeeId, today);

        int workingHours = convertToHours(totalWorkingTime);
        int overHours = convertToHours(totalOverTime);
        int overHoursWithoutNight = convertToHours(totalOverTime - totalNightTime);
        int nightHours = convertToHours(totalNightTime);
        int totalHours = convertToHours(totalWorkingTime + totalOverTime);

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

        // 이번 달 근무시간
        Map<String, Object> thisMonthAttendance = attendanceService.getThisMonthAttendance(employeeId, YearMonth.now());
        double workingTime = (double) thisMonthAttendance.get("totalTime");
        int days = (int) thisMonthAttendance.get("days");
        model.addAttribute("workingTime", workingTime);
        model.addAttribute("days", days);

        Map<String, Object> thisMonthOvertimes = overtimeService.getThisMonthOvertimes(employeeId, YearMonth.now());
        double nightTime = (double) thisMonthOvertimes.get("nightTime");
        double totalOvertime = (double) thisMonthOvertimes.get("totalTime");
        model.addAttribute("overtime", totalOvertime - nightTime);
        model.addAttribute("nightTime", nightTime);

        // 최근 수신된 요청 목록
        List<Request> requests = requestService.getMyPendingRequests(employeeId);
        model.addAttribute("requests", requests);

        // 최근 일정 목록
        List<Schedule> scheduleList = scheduleService.getSchedulesByEmployeeId(employeeId);
        List<Schedule> limitedScheduleList = scheduleList.size() > 5 ? scheduleList.subList(0, 5) : scheduleList;
        model.addAttribute("schedules", limitedScheduleList);

        // 비밀번호 변경 모달
        String result = authService.isPasswordChangeRequired();
        switch (result) {
            case "FirstChangeRequired" -> model.addAttribute("message", "FirstChangeRequired");
            case "ChangeRequired" -> model.addAttribute("message", "ChangeRequired");
            default -> model.addAttribute("message", "NoChangeRequired");
        }
        return "common/home";
    }

    // 근무 시간을 시간과 분으로 분리
    private int convertToHours(float f) {
        return (int) f;
    }
    private int convertToMinutes(float f, int hours) {
        return (int) ((f - hours) * 60);
    }

    @GetMapping("my") // 내 정보 조회
    public String viewMyInfo(Model model) {
        String employeeId = authService.getAuthenticatedUser().getUsername();
        // 내 정보 조회
        Employee employee = employeeService.getEmployeeDetails(employeeId);
        model.addAttribute("employee", employee);
        // 비밀번호 정보 조회
        Password password = authService.getPasswordInfoById(employeeId);
        model.addAttribute("password", password);

        model.addAttribute("pictureUrl", fileService.getUrl(employee.getPicture()));
        return "common/my";
    }

    @GetMapping("{employeeId}/edit") // 내 정보 수정 페이지 이동
    public String viewMyInfoEditForm(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        model.addAttribute("employee", employee);

        // 파일 관련 정보 모델에 추가
        int fileId = employee.getPicture();
        model.addAttribute("pictureUrl", fileService.getUrl(fileId));
        model.addAttribute("originalFileName", fileService.getFileInfo(fileId).getOriginalFileName());

        return "common/my-edit";
    }
}
