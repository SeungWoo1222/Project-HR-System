package com.woosan.hr_system.schedule.dao;

import com.woosan.hr_system.schedule.model.BusinessTrip;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class BusinessTripDAOImpl implements BusinessTripDAO {
    @Autowired
    private SqlSession sqlSession;
    private static final String NAMESPACE = "businessTrip.";

    // 전체 출장 목록 조회
    public List<BusinessTrip> getAllBusinessTrips(int taskId) {
        return sqlSession.selectList(NAMESPACE + "getAllBusinessTrips", taskId);
    }

    // 일정Id로 단일 출장 조회
    public BusinessTrip getBusinessTripById(int taskId) {
        return sqlSession.selectOne(NAMESPACE + "getBusinessTripById", taskId);
    }

    // tripId로 단일 출장 조회
    public BusinessTrip getBusinessTripByTripId(int tripId) {
        return sqlSession.selectOne(NAMESPACE + "getBusinessTripByTripId", tripId);
    }

    // 새로운 출장 정보 생성
    public void insertBusinessTrip(BusinessTrip businessTrip) {
        sqlSession.insert(NAMESPACE + "insertBusinessTrip", businessTrip);
    }

    // 출장 정보 업데이트
    public void updateBusinessTrip(BusinessTrip businessTrip) {
        sqlSession.update(NAMESPACE + "updateBusinessTrip", businessTrip);
    }

    // 출장 상태 변경
    public void updateTripStatus(int tripId, String status) {
        Map<String , Object> params = new HashMap<>();
        params.put("tripId", tripId);
        params.put("status", status);
        sqlSession.update(NAMESPACE + "updateTripStatus", params);
    }

    // 출장 정보 삭제
    public void deleteBusinessTrip(int tripId) {
        sqlSession.delete(NAMESPACE + "deleteBusinessTrip", tripId);
    }

    // 출장 정보 아카이브 테이블 삽입
    public void insertTripInfoInArchive(BusinessTrip businessTrip) {
        sqlSession.insert(NAMESPACE + "insertTripInfoInArchive", businessTrip);
    }
}
