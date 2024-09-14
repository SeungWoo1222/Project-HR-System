package com.woosan.hr_system.schedule.service;

import com.woosan.hr_system.schedule.dao.BusinessTripDAO;
import com.woosan.hr_system.schedule.model.BusinessTrip;
import com.woosan.hr_system.schedule.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessTripServiceImpl implements BusinessTripService {
    @Autowired
    private BusinessTripDAO businessTripDAO;

    @Override
    public List<BusinessTrip> getAllBusinessTrips() {
        return businessTripDAO.getAllBusinessTrips();
    }

    @Override
    public BusinessTrip getBusinessTripById(int mapId) {
        return businessTripDAO.getBusinessTripById(mapId);
    }

    @Override
    public void createBusinessTrip(BusinessTrip businessTrip) {
        businessTripDAO.createBusinessTrip(businessTrip);
    }

    @Override
    public void updateBusinessTrip(BusinessTrip businessTrip) {
        businessTripDAO.updateBusinessTrip(businessTrip);
    }

    @Override
    public void deleteBusinessTrip(int mapId) {
        businessTripDAO.deleteBusinessTrip(mapId);
    }

    @Override
    public List<Contact> getAllContacts() {
        return businessTripDAO.getAllContacts();
    }
}
