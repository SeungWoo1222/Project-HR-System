package com.woosan.hr_system.schedule.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.schedule.dao.BusinessTripDAO;
import com.woosan.hr_system.schedule.model.BusinessTrip;
import com.woosan.hr_system.schedule.model.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BusinessTripServiceImpl implements BusinessTripService {
    @Autowired
    private BusinessTripDAO businessTripDAO;

    @Override
    public List<BusinessTrip> getAllBusinessTrips(int taskId) {
        return businessTripDAO.getAllBusinessTrips(taskId);
    }

    @Override
    public BusinessTrip getBusinessTripById(int taskId) {
        return businessTripDAO.getBusinessTripById(taskId);
    }

    @Override
    public void insertBusinessTrip(BusinessTrip businessTrip, int taskId) {
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        LocalDateTime createdDate = userSessionInfo.getNow();

        businessTrip.setCreatedDate(createdDate);
        businessTrip.setTaskId(taskId);
        businessTripDAO.insertBusinessTrip(businessTrip);
    }


    @Override
    public void updateBusinessTrip(BusinessTrip businessTrip) {
        BusinessTrip existingBusinessTrip = businessTripDAO.getBusinessTripByTripId(businessTrip.getTripId());

        log.info("기존 출장 : {}", existingBusinessTrip);
        log.info("수정 요청 받은 출장 : {}", businessTrip);

        // 빌더 패턴을 사용하여 기존 스케줄에서 수정된 부분만 반영하여 새 객체 생성
        BusinessTrip newBusinessTrip = existingBusinessTrip.toBuilder()
                .address(Optional.ofNullable(businessTrip.getAddress()).orElse(existingBusinessTrip.getAddress()))
                .detailedAddress(Optional.ofNullable(businessTrip.getDetailedAddress()).orElse(existingBusinessTrip.getDetailedAddress()))
                .clientName(Optional.ofNullable(businessTrip.getClientName()).orElse(existingBusinessTrip.getClientName()))
                .contactTel(Optional.ofNullable(businessTrip.getContactTel()).orElse(existingBusinessTrip.getContactTel()))
                .contactEmail(Optional.ofNullable(businessTrip.getContactEmail()).orElse(existingBusinessTrip.getContactEmail()))
                .note(Optional.ofNullable(businessTrip.getNote()).orElse(existingBusinessTrip.getNote()))
                .build();

        newBusinessTrip.setTripId(existingBusinessTrip.getTripId());

        log.info("최종 업데이트할 일정: {}", newBusinessTrip);

        businessTripDAO.updateBusinessTrip(newBusinessTrip);
    }

    @Override
    public void updateTripStatus(int tripId, String status) {
        businessTripDAO.updateTripStatus(tripId, status);
    }

    @Override
    public void deleteBusinessTrip(int mapId) {
        businessTripDAO.deleteBusinessTrip(mapId);
    }

    @Override
    public void insertTripInfoInArchive(BusinessTrip businessTrip) {
        businessTripDAO.insertTripInfoInArchive(businessTrip);
    }
}
