package com.woosan.hr_system.schedule.service;

import com.woosan.hr_system.auth.model.UserSessionInfo;
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
        return scheduleDAO.getScheduleById(taskId);
    }

    @Override // 일정 등록
    public int insertSchedule(Schedule schedule) {
        // created_date, memberId 설정
        schedule.setCreatedDate(LocalDateTime.now());
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        schedule.setMemberId(userSessionInfo.getCurrentEmployeeId());
        return scheduleDAO.insertSchedule(schedule);
    }

    @Override // 일정 수정
    public void updateSchedule(Schedule schedule) {
        // 기존 스케줄 정보 가져오기
        Schedule existingSchedule = scheduleDAO.getScheduleById(schedule.getTaskId());

        log.info("기존 일정: {}", existingSchedule);
        log.info("수정 요청 받은 일정: {}", schedule);

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

        log.info("최종 업데이트할 일정: {}", newSchedule);

        // 새로운 객체로 데이터베이스 업데이트
        scheduleDAO.updateSchedule(newSchedule);
    }

    @Override // 일정 상태 변경
    public void updateScheduleStatus(int taskId, String status) {
        scheduleDAO.updateScheduleStatus(taskId, status);
    }

    @Override // 일정 삭제
    public void deleteSchedule(int taskId) {
        log.info("deleteSchedule 도착 : {}", taskId);

        Schedule schedule = scheduleDAO.getScheduleById(taskId);
        scheduleDAO.insertScheduleArchive(schedule);

        log.info("insertScheduleArchive 완료");
        BusinessTrip businessTrip = businessTripService.getBusinessTripById(taskId);
        if (businessTrip != null) {
            businessTripService.insertTripInfoInArchive(businessTrip);
            log.info("insertTripArchive 완료");
            businessTripService.deleteBusinessTrip(taskId);
            log.info("deleteBusinessTrip 완료");
        }

        scheduleDAO.deleteSchedule(taskId);
        log.info("deleteSchedule 완료");
    }

    // 일정 상태 변경 메소드 필요함
}
