package com.woosan.hr_system.notification.service;

import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Department;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.notification.dao.NotificationDAO;
import com.woosan.hr_system.notification.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private AuthService authService;
    @Autowired
    private EmployeeDAO employeeDAO;
    @Autowired
    private NotificationDAO notificationDAO;

    @Override // 내 알림 조회
    public List<Notification> getAllNotification() {
        // 현재 로그인한 사원 ID 가져오기
        String employeeId = authService.getAuthenticatedUser().getUsername();
        // 내 모든 알림 조회
        List<Notification> notificationList = notificationDAO.selectAllNotification(employeeId);
        // 상대 시간 계산 후 반환
        return notificationList.stream().peek(notification -> {
            String relativeTime = calculateRelativeTime(notification.getCreatedAt());
            notification.setRelativeTime(relativeTime);
        }).toList();
    }

    @Override // url에 저장돼있는 고유한 Id로 notificationId를 조회
    public int getNotificationId(String uniqueId) {
        return notificationDAO.getNotificationId(uniqueId);
    }

    // 상대 시간 계산
    private String calculateRelativeTime(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());

        if (duration.toDays() > 0) {
            return duration.toDays() + "일 전";
        } else if (duration.toHours() > 0) {
            return duration.toHours() + "시간 전";
        } else if (duration.toMinutes() > 0) {
            return duration.toMinutes() + "분 전";
        } else {
            return "방금 전";
        }
    }

    @Override // 읽지 않은 알림 개수 조회
    public int getUnreadCount() {
        // 현재 로그인한 사원 ID 가져오기
        String employeeId = authService.getAuthenticatedUser().getUsername();
        return notificationDAO.selectUnreadCount(employeeId);
    }

    @Override // 단일 알림 생성
    public void createNotification(String employeeId, String message, String url) {
        // 새로운 알림 객체 생성
        Notification notification = Notification.builder()
                .employeeId(employeeId)
                .message(message)
                .url(url)
                .build();
        // DB에 알림 등록
        notificationDAO.insertNotification(notification);
    }

    @Override // 다수 알림 생성
    public void createNotifications(List<String> employeeIdList, String message, String url) {
        // 새로운 알림 객체들 생성
        List<Notification> notificationList = new ArrayList<>();
        for (String employeeId : employeeIdList) {
            Notification notification = Notification.builder()
                    .employeeId(employeeId)
                    .message(message)
                    .url(url)
                    .build();
            notificationList.add(notification);
        }
        // DB에 알림들 등록
        notificationDAO.insertNotifications(notificationList);
    }

    @Override // 사원 전체 알림 생성
    public void createNotifications(String message, String url) {
        // 모든 사원 조회
        List<Employee> employeeList = employeeDAO.getAllEmployees();

        // 새로운 알림 객체들 생성
        List<Notification> notificationList = new ArrayList<>();
        for (Employee employee : employeeList) {
            Notification notification = Notification.builder()
                    .employeeId(employee.getEmployeeId())
                    .message(message)
                    .url(url)
                    .build();
            notificationList.add(notification);
        }

        // DB에 알림들 등록
        notificationDAO.insertNotifications(notificationList);
    }

    @Override // 해당 부서에게 전달할 알림 생성
    public void createNotifications(Department department, String message, String url) {
        // DB에 전달할 map 생성
        Map<String, Object> map = new HashMap<>();
        map.put("department", department);
        map.put("message", message);
        map.put("url", url);
        // DB에 알림들 등록
        notificationDAO.insertDepartmentNotifications(map);
    }

    @Override // 해당 직급에게 전달할 알림 생성
    // 여기서 position은 MANAGER, STAFF를 뜻함
    public void createNotifications(String position, String message, String url) {
        // DB에 전달할 map 생성
        Map<String, Object> map = new HashMap<>();
        map.put("position", position);
        map.put("message", message);
        map.put("url", url);
        // DB에 알림들 등록
        notificationDAO.insertPositionNotifications(map);
    }

    @Override // 내 알림 읽음 처리
    public void readNotification(int notificationId) {
        notificationDAO.markAsRead(notificationId);
    }

    @Override // 내 모든 알림 읽음 처리
    public void readAllNotification() {
        // 현재 로그인한 사원 ID 가져오기
        String employeeId = authService.getAuthenticatedUser().getUsername();
        // 모든 알림 읽음 처리
        notificationDAO.markAsReadAll(employeeId);
    }

    @Override // 내 알림 삭제
    public void removeNotification(int notificationId) {
        notificationDAO.deleteNotification(notificationId);
    }

    @Override // 내 모든 알림 삭제
    public void removeAllNotification() {
        // 현재 로그인한 사원 ID 가져오기
        String employeeId = authService.getAuthenticatedUser().getUsername();
        // 모든 알림 삭제
        notificationDAO.deleteAllNotification(employeeId);
    }

    @Override // 사원의 모든 알림 삭제
    public void removeAllNotification(String employeeId) {
        // 모든 알림 삭제
        notificationDAO.deleteAllNotification(employeeId);
    }

    // 2주일 지난 읽음 처리된 알림들 자동 삭제
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void cleanUpOldNotifications() {
        int deletedCount = notificationDAO.deleteOldNotifications();
        log.info("2주일이 지난 읽음 처리된 알림들 {}건을 자동으로 삭제하였습니다.", deletedCount);
    }
}
