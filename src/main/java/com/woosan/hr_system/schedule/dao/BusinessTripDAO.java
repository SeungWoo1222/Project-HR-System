package com.woosan.hr_system.schedule.dao;

import com.woosan.hr_system.schedule.model.BusinessTrip;
import java.util.List;

public interface BusinessTripDAO {
    List<BusinessTrip> getAllBusinessTrips(int taskId);
    BusinessTrip getBusinessTripById(int taskId);
    BusinessTrip getBusinessTripByTripId(int tripId);
    void insertBusinessTrip(BusinessTrip businessTrip);
    void updateBusinessTrip(BusinessTrip businessTrip);
    void updateTripStatus(int tripId, String status);
    void deleteBusinessTrip(int tripId);
    void insertTripInfoInArchive(BusinessTrip businessTrip);
}
