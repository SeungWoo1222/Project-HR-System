package com.woosan.hr_system.schedule.service;

import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.schedule.dao.ScheduleDAO;
import com.woosan.hr_system.schedule.model.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
    public int insertSchedule(Schedule schedule) {
        log.info("Schedule ServiceImpl 도착");
        // created_date, memberId 설정
        schedule.setCreatedDate(LocalDateTime.now());
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        schedule.setMemberId(userSessionInfo.getCurrentEmployeeId());
        return scheduleDAO.insertSchedule(schedule);
    }

    @Override // 일정 수정
    public String updateSchedule(Schedule schedule) {
        // schedule 모델에서 id 뽑아서 쓰면 됨
        // 변경사항 있는지 확인하셈
        // 내가 짠 html에선 schedule 객체가 완전하지 않음 그대로 sql 실행하면 null 부분들 존재할꺼임
        // 빌더 패턴 toBuild 메소드 이용하면 원본 객체에서 수정된 부분들만 고쳐서 새로 객체 생성할 수 있음
        scheduleDAO.updateSchedule(schedule);
        String message = "";
        return message;
    }

    @Override // 일정 삭제
    public void deleteSchedule(int taskId) {
        scheduleDAO.deleteSchedule(taskId);
    }

    // 일정 상태 변경 메소드 필요함
}
