package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.BookingDTO;
import com.cab.chaloCab.entity.Booking;
import com.cab.chaloCab.entity.Customer;
import com.cab.chaloCab.entity.Driver;
import com.cab.chaloCab.enums.BookingStatus;
import com.cab.chaloCab.repository.BookingRepository;
import com.cab.chaloCab.repository.CustomerRepository;
import com.cab.chaloCab.repository.DriverRepository;
import com.cab.chaloCab.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        Customer customer = customerRepository.findById(bookingDTO.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        Driver driver = null;
        if (bookingDTO.getDriverId() != null) {
            driver = driverRepository.findById(bookingDTO.getDriverId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));
        }

        Booking booking = Booking.builder()
                .customer(customer)
                .driver(driver)
                .pickupLocation(bookingDTO.getPickupLocation())
                .dropoffLocation(bookingDTO.getDropoffLocation())
                .fare(bookingDTO.getFare())
                .status(BookingStatus.REQUESTED)
                .assignedDriverId(bookingDTO.getAssignedDriverId())
                .build();

        Booking saved = bookingRepository.save(booking);
        return convertToDTO(saved);
    }

    @Override
    public BookingDTO updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        booking.setStatus(status);
        bookingRepository.save(booking);
        return convertToDTO(booking);
    }

    @Override
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingsByCustomerId(Long customerId) {
        return bookingRepository.findAll()
                .stream()
                .filter(b -> b.getCustomer().getId().equals(customerId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingsByDriverId(Long driverId) {
        return bookingRepository.findAll()
                .stream()
                .filter(b -> b.getDriver() != null && b.getDriver().getId().equals(driverId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        return convertToDTO(booking);
    }

    @Override
    public List<BookingDTO> getBookingHistoryByCustomer(Long customerId) {
        return getBookingsByCustomerId(customerId)
                .stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingHistoryByDriver(Long driverId) {
        return getBookingsByDriverId(driverId)
                .stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    @Override
    public String completeTrip(Long bookingId, Long driverId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!driverId.equals(booking.getAssignedDriverId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to complete this trip");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);
        return "Trip completed successfully";
    }

    @Override
    public String getDropoffLocation(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        return booking.getDropoffLocation();
    }

    @Override
    public Long getAssignedDriverId(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        return booking.getAssignedDriverId();
    }

    // Helper method to convert Booking to BookingDTO
    private BookingDTO convertToDTO(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .customerId(booking.getCustomer().getId())
                .driverId(booking.getDriver() != null ? booking.getDriver().getId() : null)
                .pickupLocation(booking.getPickupLocation())
                .dropoffLocation(booking.getDropoffLocation())
                .fare(booking.getFare())
                .status(booking.getStatus())
                .assignedDriverId(booking.getAssignedDriverId())
                .build();
    }
}
