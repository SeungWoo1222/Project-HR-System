package com.woosan.hr_system.notification.dao;

import com.woosan.hr_system.notification.model.Notification;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class NotificationDAO {

    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.notification.dao.NotificationDAO.";

    // 내 알림 조회
    public List<Notification> selectAllNotification(String employeeId) {
        return sqlSession.selectList(NAMESPACE + "selectAllNotification", employeeId);
    }
    // 읽지 않은 알림 개수 조회
    public int selectUnreadCount(String employeeId) {
        return sqlSession.selectOne(NAMESPACE + "selectUnreadCount", employeeId);
    }
    // url에 저장돼있는 고유한 Id로 notificationId를 조회
    public int getNotificationId(String uniqueId) {
        return sqlSession.selectOne(NAMESPACE + "getNotificationId", uniqueId);
    }
    // 단일 알림 생성
    public void insertNotification(Notification notification) {
        sqlSession.insert(NAMESPACE + "insertNotification", notification);
    }
    // 다수 알림 생성 (list를 통한)
    public void insertNotifications(List<Notification> notificationList) {
        sqlSession.insert(NAMESPACE + "insertNotificationsByList", notificationList);
    }
    // 부서 알림 생성
    public void insertDepartmentNotifications(Map<String, Object> map) {
        sqlSession.insert(NAMESPACE + "insertDepartmentNotifications", map);
    }
    // 직급 알림 생성
    public void insertPositionNotifications(Map<String, Object> map) {
        sqlSession.insert(NAMESPACE + "insertPositionNotifications", map);
    }
    // 내 알림 읽음 처리
    public void markAsRead(int notificationId) {
        sqlSession.update(NAMESPACE + "markAsRead", notificationId);
    }
    // 내 모든 알림 읽음 처리
    public void markAsReadAll(String employeeId) {
        sqlSession.update(NAMESPACE + "markAsReadAll", employeeId);
    }
    // 내 알림 삭제
    public void deleteNotification(int notificationId) {
        sqlSession.delete(NAMESPACE + "deleteNotification", notificationId);
    }
    // 내 모든 알림 삭제
    public void deleteAllNotification(String employeeId) {
        sqlSession.delete(NAMESPACE + "deleteAllNotification", employeeId);
    }
    // 2주일 지난 읽음 처리된 알림들 자동 삭제
    public int deleteOldNotifications() {
        return sqlSession.delete(NAMESPACE + "deleteOldNotifications");
    }
}
