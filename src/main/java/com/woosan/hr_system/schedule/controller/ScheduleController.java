package com.woosan.hr_system.schedule.controller;

import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.schedule.model.Schedule;
import com.woosan.hr_system.schedule.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;
//=============================================생성 메소드================================================================
    @GetMapping("/showCreatePage") // 보고서 생성 페이지 이동
    public String showCreatePage(Model model) {
        model.addAttribute("schedule", new Schedule());
        return "schedule/create";
    }

    @PostMapping// 보고서 생성
    public ResponseEntity<String> createSchedule(@RequestBody Schedule schedule)  {
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String memberId = userSessionInfo.getCurrentEmployeeId();
        LocalDateTime currentTime = userSessionInfo.getNow();
        schedule.setMemberId(memberId);
        schedule.setCreatedDate(currentTime);

        scheduleService.insertSchedule(schedule);

        return ResponseEntity.ok("일정 등록이 완료되었습니다.");
    }
//=============================================생성 메소드================================================================
//=============================================조회 메소드================================================================
    @GetMapping
    public String getAllSchedules(Model model) {
        List<Schedule> Schedules = scheduleService.getAllSchedules();
        model.addAttribute("Schedules", Schedules);
        return "scheduleList";
    }

    @GetMapping("/{taskId}")
    public String getScheduleById(@PathVariable("taskId") int taskId, Model model) {
        Schedule schedule = scheduleService.getScheduleById(taskId);
        model.addAttribute("schedule", schedule);
        return "scheduleDetail";
    }
//=============================================조회 메소드================================================================
//=============================================수정 메소드================================================================
    @PutMapping("/{taskId}")
    public void updateSchedule(@RequestBody Schedule schedule)  {
        scheduleService.updateSchedule(schedule);
    }
//=============================================수정 메소드================================================================
//=============================================삭제 메소드================================================================
    @DeleteMapping("/{taskId}")
    public void deleteSchedule(@PathVariable("taskId") int taskId) {
        scheduleService.deleteSchedule(taskId);
    }
//=============================================삭제 메소드================================================================
}
