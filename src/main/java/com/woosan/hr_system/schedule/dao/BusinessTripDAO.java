package com.woosan.hr_system.schedule.dao;

import com.woosan.hr_system.schedule.model.BusinessTrip;
import com.woosan.hr_system.schedule.model.Contact;
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
    public List<BusinessTrip> getAllBusinessTrips() {
        return sqlSession.selectList(NAMESPACE + "getAllBusinessTrips");
    }

    // 출장 ID로 단일 출장 조회
    public BusinessTrip getBusinessTripById(int mapId) {
        return sqlSession.selectOne(NAMESPACE + "getBusinessTripById", mapId);
    }

    // 새로운 출장 정보 생성
    public void createBusinessTrip(BusinessTrip businessTrip) {
        sqlSession.insert(NAMESPACE + "createBusinessTrip", businessTrip);
    }

    // 출장 정보 업데이트
    public void updateBusinessTrip(BusinessTrip businessTrip) {
        sqlSession.update(NAMESPACE + "updateBusinessTrip", businessTrip);
    }

    // 출장 정보 삭제
    public void deleteBusinessTrip(int mapId) {
        sqlSession.delete(NAMESPACE + "deleteBusinessTrip", mapId);
    }

    // 모든 연락처 목록 조회 (contacts)
    public List<Contact> getAllContacts() {
        return sqlSession.selectList(NAMESPACE + "getAllContacts");
    }
}
