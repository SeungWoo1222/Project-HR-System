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
    public void insertSchedule(Schedule schedule) {
        scheduleDAO.insertSchedule(schedule);
    }

    @Override
    public List<Schedule> getAllSchedules() {
        return scheduleDAO.getAllSchedules();
    }

    @Override
    public Schedule getScheduleById(int taskId) {
        return scheduleDAO.getScheduleById(taskId);
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
