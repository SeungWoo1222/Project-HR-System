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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.time.LocalDateTime;
import java.util.*;

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
    public ResponseEntity<String> insertBusinessTrip(BusinessTrip businessTrip, int taskId) {
        // 이메일 도메인 부분 추출
        String domain = businessTrip.getContactEmail().substring(businessTrip.getContactEmail().indexOf("@") + 1);

        boolean validEmailresult = isValidEmailDomain(domain);
        if (!validEmailresult) {
            return ResponseEntity.badRequest().body("유효하지 않은 이메일 도메인입니다.");
        }

        businessTrip.setCreatedDate(LocalDateTime.now());
        businessTrip.setTaskId(taskId);
        businessTripDAO.insertBusinessTrip(businessTrip);
        return ResponseEntity.ok("일정 생성이 완료되었습니다.");
    }

    public boolean isValidEmailDomain(String domain) {
        try {
            // DNS 설정
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
            DirContext ictx = new InitialDirContext(env);

            // MX 레코드 조회
            Attributes attributes = ictx.getAttributes(domain, new String[]{"MX"});
            return attributes.size() > 0; // MX 레코드가 있으면 유효한 도메인
        } catch (NamingException e) {
            log.error("DNS 조회 실패 - 도메인: {}, 오류: {}", domain, e.getMessage());
            return false; // 도메인 또는 네트워크 오류
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생 - 도메인: {}, 오류: {}", domain, e.getMessage());
            return false; // 알 수 없는 오류 처리
        }
    }

    @Override
    public ResponseEntity <String> updateBusinessTrip(BusinessTrip businessTrip) {
        log.info("taskId 확인 : {}", businessTrip.getTaskId());
        BusinessTrip existingBusinessTrip = businessTripDAO.getBusinessTripById(businessTrip.getTaskId());
        log.info("existingBusinessTrip 호출 : {}", existingBusinessTrip);

        if (Objects.nonNull(existingBusinessTrip)) {
            if (Objects.isNull(businessTrip.getAddress())) {
                log.info("O X인 경우");
                businessTripDAO.deleteBusinessTrip(existingBusinessTrip.getTripId());
            } else {
                log.info("O O인 경우");
                // 이메일 도메인 유효성 검사
                String domain = businessTrip.getContactEmail().substring(businessTrip.getContactEmail().indexOf("@") + 1);

                boolean validEmailresult = isValidEmailDomain(domain);
                if (!validEmailresult) {
                    return ResponseEntity.badRequest().body("유효하지 않은 이메일 도메인입니다.");
                }

                // 빌더 패턴을 사용하여 기존 스케줄에서 수정된 부분만 반영하여 새 객체 생성
                BusinessTrip newBusinessTrip = existingBusinessTrip.toBuilder()
                        .address(Optional.ofNullable(businessTrip.getAddress()).orElse(existingBusinessTrip.getAddress()))
                        .detailedAddress(Optional.ofNullable(businessTrip.getDetailedAddress()).orElse(existingBusinessTrip.getDetailedAddress()))
                        .tripName(Optional.ofNullable(businessTrip.getTripName()).orElse(existingBusinessTrip.getTripName()))
                        .contactTel(Optional.ofNullable(businessTrip.getContactTel()).orElse(existingBusinessTrip.getContactTel()))
                        .contactEmail(Optional.ofNullable(businessTrip.getContactEmail()).orElse(existingBusinessTrip.getContactEmail()))
                        .note(Optional.ofNullable(businessTrip.getNote()).orElse(existingBusinessTrip.getNote()))
                        .build();

                newBusinessTrip.setTripId(existingBusinessTrip.getTripId());

                businessTripDAO.updateBusinessTrip(newBusinessTrip);
            }
        } else {
            log.info("X O인 경우");
            // 이메일 도메인 유효성 검사
            String domain = businessTrip.getContactEmail().substring(businessTrip.getContactEmail().indexOf("@") + 1);

            boolean validEmailresult = isValidEmailDomain(domain);
            if (!validEmailresult) {
                return ResponseEntity.badRequest().body("유효하지 않은 이메일 도메인입니다.");
            }

            businessTrip.setCreatedDate(LocalDateTime.now());
            businessTripDAO.insertBusinessTrip(businessTrip);
        }

        return ResponseEntity.ok("일정 수정이 완료되었습니다.");
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
