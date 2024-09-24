package com.woosan.hr_system.attendance.service;

import com.woosan.hr_system.aspect.LogAfterExecution;
import com.woosan.hr_system.aspect.LogBeforeExecution;
import com.woosan.hr_system.attendance.dao.OvertimeDAO;
import com.woosan.hr_system.attendance.model.Overtime;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.common.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

@Slf4j
@Service
public class OvertimeServiceImpl implements OvertimeService{
    @Autowired
    private OvertimeDAO overtimeDAO;
    @Autowired
    private AuthService authService;
    @Autowired
    private CommonService commonService;

    @Override // ID를 이용한 초과근무 조회
    public Overtime getOvertimeById(int overtimeId) {
        Overtime overtime = overtimeDAO.getOvertimeById(overtimeId);
        if (overtime == null) {
            throw new IllegalArgumentException("해당 초과근무 정보를 찾을 수 없습니다.\n초과근무 ID : " + overtimeId);
        }
        return overtime;
    }

    @Override // 모든 초과근무 조회
    public List<Overtime> getAllOvertimes() {
        return overtimeDAO.getAllOvertimes();
    }

    @Override // 사원의 이번 달 초과근무 총 시간 조회
    public Map<String, Object> getThisMonthOvertimes(String employeeId, YearMonth yearMonth) {
        // 사원의 이번 달 초과근무 조회
        List<Overtime> overtimeList = overtimeDAO.getThisMonthOvertimes(employeeId, yearMonth);

        // 사원의 이번달 총 초과근무 시간 계산
        double totalOvertime = overtimeList.stream()
                .mapToDouble(Overtime::getTotalHours)
                .sum();

        // 사원의 이번달 총 야간근무 시간 계산
        double totalNightOvertime = overtimeList.stream()
                .mapToDouble(Overtime::getNightHours)
                .sum();

        // 총 시간, 총 야간 시간, 초과근무 일수 반환
        return Map.of("totalTime", totalOvertime,
                "nightTime", totalNightOvertime,
                "days", overtimeList.size());
    }

    @Override // 사원의 이번 주 초과근무 총 시간 조회
    public float getTotalWeeklyOvertime(String employeeId, LocalDate date) {
        return overtimeDAO.getTotalWeeklyOvertime(employeeId, date);
    }

    @LogBeforeExecution
    @LogAfterExecution
    @Override // 초과근무 등록
    public String addOvertime(int attendanceId, LocalTime startTime, LocalTime endTime) {
        String employeeId = authService.getAuthenticatedUser().getUsername();

        // 총 초과 근무 시간 설정
        Map<String, Object> map = setTotalHours(employeeId, startTime, endTime);
        float overtimeHours = (float) map.get("overtimeHours");
        endTime = (LocalTime) map.get("endTime");

        // 야간 근무 시간 설정
        float nightHours = setNightHours(endTime);

        // 초과근무 객체 생성 후 등록
        Overtime overtime = Overtime.builder()
                .employeeId(employeeId)
                .date(LocalDate.now())
                .startTime(startTime)
                .endTime(endTime)
                .nightHours(nightHours)
                .totalHours(overtimeHours)
                .build();

        overtimeDAO.insertOvertime(overtime);

        return authService.getAuthenticatedUser().getNameWithId() + "사원의 새로운 초과근무(" + overtimeHours + "시간)가 등록되었습니다.";
    }

    // 총 초과근무 시간 설정
    private Map<String, Object> setTotalHours(String employeeId, LocalTime startTime, LocalTime endTime) {
        // 이번 주 초과근무 시간 조회
        float totalWeeklyOvertime = getTotalWeeklyOvertime(employeeId, LocalDate.now());

        // 금일 총 초과근무 시간 계산
        Duration overtimeDuration = Duration.between(startTime, endTime);
        float overtimeHours = overtimeDuration.toMinutes() / 60.00f;

        // 이번 주 초과근무 시간과 금일 초과 근무 시간의 합계가 12시간이 초과하는지 조회
        if (totalWeeklyOvertime + overtimeHours > 12.0) {
            // 초과된 시간 계산
            float excessOvertime = (totalWeeklyOvertime + overtimeHours) - 12.0f;

            // 초과된 시간만큼 초과근무 시간과 종료시간 조정
            overtimeHours -= excessOvertime;
            endTime = endTime.minusMinutes((long) (excessOvertime * 60.0));
        }

        return Map.of("overtimeHours", overtimeHours,
                "endTime", endTime);
    }

    // 야간근무 시간 설정
    private float setNightHours(LocalTime endTime) {
        float nightHours = 0;

        // 기준 야간근무 시작 시간 (저녁 10시)
        LocalTime nightStartTime = LocalTime.of(22, 0); // 22:00 기준

        // 초과근무 종료 시간이 22:00를 초과하면 야간 근무시간 측정
        if (endTime.isAfter(nightStartTime)) {
            Duration nightDuration = Duration.between(nightStartTime, endTime);
            nightHours = nightDuration.toMinutes() / 60.00f;
        }
        return nightHours;
    }

    @Override // 초과근무 수정
    public String editOvertime(Overtime overtime) {
        // 초과근무 원본 조회
        Overtime originalOvertime = getOvertimeById(overtime.getOvertimeId());

        // 변경사항 확인
        checkForOvertimeChanges(originalOvertime, overtime);

        // 총 초과 근무 시간 설정
        Map<String, Object> map = setTotalHours(overtime.getEmployeeId(), overtime.getStartTime(), overtime.getEndTime());
        float overtimeHours = (float) map.get("overtimeHours");
        LocalTime endTime = (LocalTime) map.get("endTime");

        // 야간 근무 시간 설정
        float nightHours = setNightHours(endTime);

        // 초과근무 객체 생성 후 등록
        Overtime updatedOvertime = overtime.toBuilder()
                .endTime(endTime)
                .nightHours(nightHours)
                .totalHours(overtimeHours)
                .build();

        overtimeDAO.updateOvertime(updatedOvertime);

        return "초과근무('" + overtime.getOvertimeId() + "')가 수정되었습니다.";
    }

    // Overtime 특정 필드만 비교하도록 필드 이름을 Set으로 전달하는 메소드
    private void checkForOvertimeChanges(Overtime original, Overtime updated) {
        Set<String> fieldsToCompare = new HashSet<>(Arrays.asList(
                "startTime", "endTime"
        ));
        commonService.processFieldChanges(original, updated, fieldsToCompare);
    }

    @Override // 초과근무 삭제
    public String deleteOvertime(int overtimeId) {
        // 초과근무 조회
        if (overtimeDAO.getOvertimeById(overtimeId) == null) {
            throw new IllegalArgumentException("해당 초과근무 정보를 찾을 수 없습니다.\n초과근무 ID : " + overtimeId);
        }

        overtimeDAO.deleteOvertime(overtimeId);

        return "초과근무('" + overtimeId + "')가 삭제되었습니다.";
    }
}
