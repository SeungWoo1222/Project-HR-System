package com.woosan.hr_system.schedule.service;

import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.notification.service.NotificationService;
import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.schedule.dao.ScheduleDAO;
import com.woosan.hr_system.schedule.model.BusinessTrip;
import com.woosan.hr_system.schedule.model.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleDAO scheduleDAO;
    @Autowired
    private BusinessTripService businessTripService;
    @Autowired
    private NotificationService notificationService;

    @Override
    public List<Schedule> getAllSchedules() {
        return scheduleDAO.getAllSchedules();
    }

    @Override // 사원의 모든 일정 조회
    public List<Schedule> getSchedulesByEmployeeId(String employeeId) {
        return scheduleDAO.getSchedulesByEmployeeId(employeeId);
    }

    @Override
    public Schedule getScheduleById(int taskId) {
        Schedule schedule = checkScheduleAuthorization(taskId);
        return schedule;
    }

    @Override // 일정 등록
    public int insertSchedule(Schedule schedule) {
        // created_date, memberId 설정
        schedule.setCreatedDate(LocalDateTime.now());
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        schedule.setMemberId(userSessionInfo.getCurrentEmployeeId());

        boolean validTimeResult = isValidTimeRange(schedule);
        if (!validTimeResult) {
            throw new IllegalArgumentException("시작일이 종료일보다 빠르거나 같아야 합니다.");
        }

        return scheduleDAO.insertSchedule(schedule);
    }

    public boolean isValidTimeRange(Schedule schedule) {
        return schedule.getStartTime() != null && schedule.getEndTime() != null && !schedule.getStartTime().isAfter(schedule.getEndTime());
    }

    @Override // 일정 수정
    public void updateSchedule(Schedule schedule) {
        checkScheduleAuthorization(schedule.getTaskId());
        // 기존 스케줄 정보 가져오기
        Schedule existingSchedule = scheduleDAO.getScheduleById(schedule.getTaskId());

        // 빌더 패턴을 사용하여 기존 스케줄에서 수정된 부분만 반영하여 새 객체 생성
        Schedule newSchedule = existingSchedule.toBuilder()
                .taskName(Optional.ofNullable(schedule.getTaskName()).orElse(existingSchedule.getTaskName()))
                .content(Optional.ofNullable(schedule.getContent()).orElse(existingSchedule.getContent()))
                .startTime(Optional.ofNullable(schedule.getStartTime()).orElse(existingSchedule.getStartTime()))
                .endTime(Optional.ofNullable(schedule.getEndTime()).orElse(existingSchedule.getEndTime()))
                .allDay(schedule.isAllDay())
                .color(Optional.ofNullable(schedule.getColor()).orElse(existingSchedule.getColor()))
                .build();

        newSchedule.setTaskId(existingSchedule.getTaskId());

        // 새로운 객체로 데이터베이스 업데이트
        scheduleDAO.updateSchedule(newSchedule);
    }

    @Override // 일정 상태 변경
    public void updateScheduleStatus(int taskId, String status, String taskName) {
        checkScheduleAuthorization(taskId);
        if ("완료".equals(status)) {
            UserSessionInfo userSessionInfo = new UserSessionInfo();
            String employeeId = userSessionInfo.getCurrentEmployeeId();
            notificationService.createNotification(employeeId, taskName + " 일정이 완료되었습니다. 보고서를 작성해주세요.", "/report/writeFromSchedule" + taskId);
        }
        scheduleDAO.updateScheduleStatus(taskId, status);
    }

    @Override // 일정 삭제
    public void deleteSchedule(int taskId) {
        checkScheduleAuthorization(taskId);

        Schedule schedule = scheduleDAO.getScheduleById(taskId);
        scheduleDAO.insertScheduleArchive(schedule);

        BusinessTrip businessTrip = businessTripService.getBusinessTripById(taskId);
        if (businessTrip != null) {
            businessTripService.insertTripInfoInArchive(businessTrip);
            businessTripService.deleteBusinessTrip(taskId);
        }

        scheduleDAO.deleteSchedule(taskId);
    }

    // 일정에 대한 접근 권한, 일정이 있는지 확인
    public Schedule checkScheduleAuthorization(int taskId) {
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String currentEmployeeId = userSessionInfo.getCurrentEmployeeId();
        Schedule schedule = scheduleDAO.getScheduleById(taskId); // 요청 세부 정보 가져오기
        if (schedule == null) {
            throw new IllegalArgumentException("해당 일정이 존재하지 않습니다.\n일정 ID : " + taskId);
        }

        // 작성자와 로그인한 사용자가 동일하지 않으면 권한 오류 발생
        if (!schedule.getMemberId().equals(currentEmployeeId)) {
            throw new SecurityException("권한이 없습니다.");
        }

        return schedule; // 요청 정보 반환
    }
}
