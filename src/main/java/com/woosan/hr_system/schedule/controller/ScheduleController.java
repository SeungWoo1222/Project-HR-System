package com.woosan.hr_system.schedule.controller;

import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.schedule.model.BusinessTrip;
import com.woosan.hr_system.schedule.model.Schedule;
import com.woosan.hr_system.schedule.service.BusinessTripService;
import com.woosan.hr_system.schedule.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private BusinessTripService businessTripService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private AuthService authService;

    @GetMapping("/{employeeId}/list") // 사원의 모든 일정 조회
    public String viewScheduleList(@PathVariable("employeeId") String employeeId, Model model) {
        // 사원의 일정 조회
        List<Schedule> scheduleList = scheduleService.getSchedulesByEmployeeId(employeeId);
        model.addAttribute("scheduleList", scheduleList);

        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 사원의 오늘의 일정 필터링 (start_time end_time 사이에 포함되는 일정)
        List<Schedule> todaySchedules = scheduleList.stream()
                .filter(schedule ->
                        (schedule.getStartTime().toLocalDate().equals(today) ||
                                schedule.getEndTime().toLocalDate().equals(today)) ||
                                (schedule.getStartTime().toLocalDate().isBefore(today)
                                        && schedule.getEndTime().toLocalDate().isAfter(today)))
                .toList();
        model.addAttribute("todaySchedules", todaySchedules);
        return "schedule/list";
    }

    @GetMapping("/{taskId}") // 일정 세부 조회
    public String viewScheduleDetail(@PathVariable("taskId") int taskId, Model model) {
        Schedule scheduleInfo = scheduleService.getScheduleById(taskId);
        // 일정 세부 정보
        model.addAttribute("schedule", scheduleInfo);
        // 사원 정보
        model.addAttribute("employee", employeeService.getEmployeeById(scheduleInfo.getMemberId()));

        // 해당 일정과 연관된 출장 정보만 가져오기
        List<BusinessTrip> trips = businessTripService.getAllBusinessTrips(taskId);
        model.addAttribute("trips", trips);
        return "schedule/detail";
    }

    @GetMapping("/new") // 일정 등록 페이지
    public String viewScheduleForm(Model model) {
        model.addAttribute("employeeList", employeeService.getAllEmployee());
        return "schedule/new";
    }

    @GetMapping("/new2") // 본인 일정 등록 페이지
    public String newScheduleForm(Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(authService.getAuthenticatedUser().getUsername()));
        return "schedule/new2";
    }

    @GetMapping("/{taskId}/edit") // 일정 수정 페이지
    public String newScheduleForm(@PathVariable("taskId") int taskId, Model model) {
        log.info("일정 수정 컨트롤러 호출");
        Schedule scheduleInfo = scheduleService.getScheduleById(taskId);
        model.addAttribute("schedule", scheduleInfo);
        List<BusinessTrip> businessTrip = businessTripService.getAllBusinessTrips(taskId);
        model.addAttribute("trips", businessTrip);
        model.addAttribute("employee", employeeService.getEmployeeById(scheduleInfo.getMemberId()));
        return "schedule/edit";
    }

    @PostMapping // 일정 등록
    public ResponseEntity<String> insertSchedule(@ModelAttribute Schedule schedule,
                                                 @RequestParam("tripInfo") String tripInfoJson) {
        log.info("insert schedule: {}", schedule);
        log.info("insert tripInfoJson: {}", tripInfoJson);

        int taskId = scheduleService.insertSchedule(schedule);
        log.info("taskId 반환 완료 : {}", taskId);
        if (tripInfoJson != null) {
            log.info("tripInfoJson 기반 출장정보 삽입 시작");
            businessTripService.insertBusinessTrip(tripInfoJson, taskId);
        }

        return ResponseEntity.ok("일정 생성이 완료되었습니다.");
    }

    @PutMapping // 일정 수정
    public ResponseEntity<String> updateSchedule(@ModelAttribute Schedule schedule) {
        return ResponseEntity.ok(scheduleService.updateSchedule(schedule));
    }

    @DeleteMapping("/{taskId}")
    public void deleteSchedule(@PathVariable("taskId") int taskId) {
        scheduleService.deleteSchedule(taskId);
    }

    // 출장지, 거래처 정보 입력하는 모달 창 생성
    @GetMapping("/trip")
    public String viewTrip() {
        return "schedule/trip";
    }
}
