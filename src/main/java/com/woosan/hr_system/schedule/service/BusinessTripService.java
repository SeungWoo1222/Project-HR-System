package com.woosan.hr_system.schedule.service;

import com.woosan.hr_system.schedule.model.BusinessTrip;
import com.woosan.hr_system.schedule.model.Contact;

import java.util.List;

public interface BusinessTripService {
    List<BusinessTrip> getAllBusinessTrips();

    BusinessTrip getBusinessTripById(int mapId);

    void createBusinessTrip(BusinessTrip businessTrip);

    void updateBusinessTrip(BusinessTrip businessTrip);

    void deleteBusinessTrip(int mapId);

    List<Contact> getAllContacts();
}
