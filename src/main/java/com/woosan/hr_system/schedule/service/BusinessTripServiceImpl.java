package com.woosan.hr_system.schedule.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.schedule.dao.BusinessTripDAO;
import com.woosan.hr_system.schedule.model.BusinessTrip;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public void insertBusinessTrip(String tripInfoJson, int taskId) {
        log.info("insertBusinessTrip serviceImpl 도착 ");
        ObjectMapper objectMapper = new ObjectMapper();
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        LocalDateTime createdDate = userSessionInfo.getNow();

        try {
            List<BusinessTrip> businessTrips = objectMapper.readValue(tripInfoJson, new TypeReference<List<BusinessTrip>>() {
            });
            // 각 BusinessTrip 객체를 저장하고 mapId를 리스트에 추가
            for (BusinessTrip businessTrip : businessTrips) {
                businessTrip.setCreatedDate(createdDate);
                businessTrip.setTaskId(taskId);
                log.info("service businessTrip 구조 : {}", businessTrip);
                businessTripDAO.insertBusinessTrip(businessTrip);
            }
        } catch (Exception e) {
            log.info("JSON 변환 오류");
            e.printStackTrace();
        }
    }


    @Override
    public void updateBusinessTrip(BusinessTrip businessTrip) {
        businessTripDAO.updateBusinessTrip(businessTrip);
    }

    @Override
    public void deleteBusinessTrip(int mapId) {
        businessTripDAO.deleteBusinessTrip(mapId);
    }
}
