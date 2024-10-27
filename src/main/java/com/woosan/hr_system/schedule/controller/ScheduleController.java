package com.woosan.hr_system.schedule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.schedule.model.BusinessTrip;
import com.woosan.hr_system.holiday.service.HolidayService;
import com.woosan.hr_system.schedule.model.Schedule;
import com.woosan.hr_system.schedule.service.BusinessTripService;
import com.woosan.hr_system.schedule.service.ScheduleService;
import com.woosan.hr_system.survey.model.Response;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    @Autowired
    private HolidayService holidayService;

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
        // 모든 공휴일 모델에 추가
        model.addAttribute("holidays", holidayService.getAllHoliday());
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
        BusinessTrip trip = businessTripService.getBusinessTripById(taskId);
        model.addAttribute("trip", trip);
        return "schedule/detail";
    }

    @GetMapping("/new") // 일정 등록 페이지
    public String viewScheduleForm(Model model) {
        model.addAttribute("employeeList", employeeService.getAllEmployee());
        return "schedule/new";
    }

    @GetMapping("/new2") // 본인 일정 등록 페이지
    public String viewMyScheduleForm(Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(authService.getAuthenticatedUser().getUsername()));
        return "schedule/new2";
    }

    @GetMapping("/{taskId}/edit") // 일정 수정 페이지
    public String viewEditScheduleForm (@PathVariable("taskId") int taskId, Model model) {
        Schedule scheduleInfo = scheduleService.getScheduleById(taskId);
        model.addAttribute("schedule", scheduleInfo);
        BusinessTrip trip = businessTripService.getBusinessTripById(taskId);

        if (trip != null) {
            String contactEmail = trip.getContactEmail();
            if (contactEmail != null && contactEmail.contains("@")) {
                String[] emailParts = contactEmail.split("@");
                model.addAttribute("emailLocalPart", emailParts[0]);
                model.addAttribute("emailDomainPart", emailParts[1]);
            } else {
                model.addAttribute("emailLocalPart", "");
                model.addAttribute("emailDomainPart", "");
            }
            model.addAttribute("trip", trip);
        }

        model.addAttribute("employee", employeeService.getEmployeeById(scheduleInfo.getMemberId()));
        return "schedule/edit";
    }

    @PostMapping // 일정 등록
    public ResponseEntity<String> insertSchedule(@Valid @ModelAttribute Schedule schedule,
                                                 @Valid @ModelAttribute BusinessTrip businessTrip,
                                                 BindingResult bindingResult) {
        // 유효성 검사가 실패했을 경우 처리
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(422).body(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        int taskId = scheduleService.insertSchedule(schedule);

        if (businessTrip.getAddress() != null) {
            ResponseEntity<String> response = businessTripService.insertBusinessTrip(businessTrip, taskId);
            return response;
        }

        return ResponseEntity.ok("일정 생성이 완료되었습니다.");
    }

    @Transactional
    @PutMapping("/edit") // 일정 수정
    public ResponseEntity<String> updateSchedule(@Valid @ModelAttribute Schedule schedule,
                                                 @Valid @ModelAttribute BusinessTrip businessTrip,
                                                 BindingResult bindingResult) {
        // 유효성 검사가 실패했을 경우 처리
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(422).body(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        scheduleService.updateSchedule(schedule);
        // 출장이 없던 일정에 출장지가 생긴 경우를 대비하여 taskId를 설정해줌
        businessTrip.setTaskId(schedule.getTaskId());

        ResponseEntity<String> response =  businessTripService.updateBusinessTrip(businessTrip);
        return response;
    }

    // 일정 삭제
    @Transactional
    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<String> deleteSchedule(@PathVariable("taskId") int taskId) {
        scheduleService.deleteSchedule(taskId);
        return ResponseEntity.ok("일정 삭제가 완료되었습니다.");
    }

    // 일정 상태 변경
    @PutMapping("/status/{taskId}")
    public ResponseEntity<String> updateScheduleStatus(@PathVariable("taskId") int taskId,
                                                       @RequestParam("status") String status,
                                                       @RequestParam("taskName") String taskName) {
        // 서비스 호출하여 상태 업데이트
        scheduleService.updateScheduleStatus(taskId, status, taskName);
        return ResponseEntity.ok("일정 상태 변경이 완료되었습니다.");
    }

    // 출장 상태 변경
    @PutMapping("/tripStatus/{tripId}")
    public ResponseEntity<String> updateTripStatus(@PathVariable("tripId") int tripId,
                                                   @RequestBody Map<String, String> requestBody) {
        String status = requestBody.get("status");
        businessTripService.updateTripStatus(tripId, status);
        return ResponseEntity.ok("출장 상태 변경이 완료되었습니다.");
    }
}
