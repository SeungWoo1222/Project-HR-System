package com.woosan.hr_system.schedule.service;

import com.woosan.hr_system.schedule.dao.ScheduleDAO;
import com.woosan.hr_system.schedule.model.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public void insertSchedule(Schedule schedule) {
        scheduleDAO.insertSchedule(schedule);
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
