package com.woosan.hr_system.schedule.controller;

import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.schedule.model.Schedule;
import com.woosan.hr_system.schedule.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping
    public List<Schedule> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/{taskId}")
    public Schedule getScheduleById(@RequestParam("taskId") int taskId) {
        return scheduleService.getScheduleById(taskId);
    }

    @PostMapping
    public ResponseEntity<String> insertSchedule(@RequestPart(value="schedule") Schedule schedule) {
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String memberId = userSessionInfo.getCurrentEmployeeId();
        LocalDateTime currentTime = userSessionInfo.getNow();
        schedule.setMemberId(memberId);
        schedule.setCreatedDate(currentTime);

        scheduleService.insertSchedule(schedule);

        return ResponseEntity.ok("보고서 작성이 완료되었습니다.");
    }

    @PutMapping("/{taskId}")
    public void updateSchedule(@PathVariable("taskId") int taskId, @RequestBody Schedule schedule) {
        schedule.setTaskId(taskId);
        scheduleService.updateSchedule(schedule);
    }

    @DeleteMapping("/{taskId}")
    public void deleteSchedule(@PathVariable("taskId") int taskId) {
        scheduleService.deleteSchedule(taskId);
    }
}
