package com.woosan.hr_system.schedule.dao;

import com.woosan.hr_system.schedule.model.BusinessTrip;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class BusinessTripDAO {
    @Autowired
    private SqlSession sqlSession;
    private static final String NAMESPACE = "com.woosan.hr_system.schedule.dao.BusinessTripDAO.";

    // 전체 출장 목록 조회
    public List<BusinessTrip> getAllBusinessTrips(int taskId) {
        return sqlSession.selectList(NAMESPACE + "getAllBusinessTrips", taskId);
    }

    // 일정Id로 단일 출장 조회
    public BusinessTrip getBusinessTripById(int taskId) {
        return sqlSession.selectOne(NAMESPACE + "getBusinessTripById", taskId);
    }

    // 새로운 출장 정보 생성
    public void insertBusinessTrip(BusinessTrip businessTrip) {
        log.info("insetBusinessTrip DAO 도착");
        sqlSession.insert(NAMESPACE + "insertBusinessTrip", businessTrip);
    }

    // 출장 정보 업데이트
    public void updateBusinessTrip(BusinessTrip businessTrip) {
        sqlSession.update(NAMESPACE + "updateBusinessTrip", businessTrip);
    }

    // 출장 정보 삭제
    public void deleteBusinessTrip(int mapId) {
        sqlSession.delete(NAMESPACE + "deleteBusinessTrip", mapId);
    }
}
