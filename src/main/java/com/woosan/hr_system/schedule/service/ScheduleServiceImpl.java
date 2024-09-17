package com.woosan.hr_system.schedule.service;

import com.woosan.hr_system.schedule.dao.ScheduleDAO;
import com.woosan.hr_system.schedule.model.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleDAO scheduleDAO;

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
    public String insertSchedule(Schedule schedule) {
        // created_date 설정
        schedule.setCreatedDate(LocalDateTime.now());

        // 일정 등록
        scheduleDAO.insertSchedule(schedule);

        // 알림 전송 후 메세지 반환
        String message = "새로운 일정이 등록되었습니다."
                + "\n담당자 : " + schedule.getMemberId()
                + "\n일정 이름 : " + schedule.getTaskName();
        return message;
    }

    @Override
    public void updateSchedule(Schedule schedule) {
        scheduleDAO.updateSchedule(schedule);
    }

    @Override
    public void deleteSchedule(int taskId) {
        scheduleDAO.deleteSchedule(taskId);
    }
}
