package com.cab.chaloCab.service;

public interface RideAssignmentService {
    String assignDriverToBooking(Long bookingId);
    String driverAcceptBooking(Long bookingId, Long driverId);
}
