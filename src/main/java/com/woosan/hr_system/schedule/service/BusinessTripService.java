package com.woosan.hr_system.schedule.service;

import com.woosan.hr_system.schedule.model.BusinessTrip;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BusinessTripService {
    List<BusinessTrip> getAllBusinessTrips(int taskId);

    BusinessTrip getBusinessTripById(int taskId);

    ResponseEntity <String> insertBusinessTrip(BusinessTrip businessTrip, int taskId);

    ResponseEntity<String> updateBusinessTrip(BusinessTrip businessTrip);

    void updateTripStatus(int tripId, String status);

    void deleteBusinessTrip(int mapId);

    void insertTripInfoInArchive(BusinessTrip businessTrip);
}
