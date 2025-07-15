package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.entity.Booking;
import com.cab.chaloCab.entity.Driver;
import com.cab.chaloCab.enums.BookingStatus;
import com.cab.chaloCab.enums.DriverStatus;
import com.cab.chaloCab.repository.BookingRepository;
import com.cab.chaloCab.repository.DriverRepository;
import com.cab.chaloCab.service.RideAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class RideAssignmentServiceImpl implements RideAssignmentService {

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private DriverRepository driverRepo;

    @Override
    public String assignDriverToBooking(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getAssignedDriverId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver already assigned");
        }

        // Simulate finding nearest driver (pick any available driver)
        List<Driver> drivers = driverRepo.findAll();
        Driver availableDriver = drivers.stream()
                .filter(d -> d.getStatus() == DriverStatus.APPROVED)
                .min(Comparator.comparing(Driver::getId)) // Simulate "nearest"
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No available drivers"));

        booking.setAssignedDriverId(availableDriver.getId());
        booking.setStatus(BookingStatus.ACCEPTED);
        bookingRepo.save(booking);

        return "Driver " + availableDriver.getName() + " assigned to booking ID " + booking.getId();
    }

    @Override
    public String driverAcceptBooking(Long bookingId, Long driverId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!driverId.equals(booking.getAssignedDriverId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unauthorized driver");
        }

        booking.setStatus(BookingStatus.ONGOING);
        bookingRepo.save(booking);

        return "Driver " + driverId + " accepted booking " + bookingId;
    }
}
