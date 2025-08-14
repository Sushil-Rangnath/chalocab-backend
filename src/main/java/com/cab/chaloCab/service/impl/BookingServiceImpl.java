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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
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
                .outsideStation(bookingDTO.getOutsideStation())
                .sourceLocation(bookingDTO.getSourceLocation())
                .destinationLocation(bookingDTO.getDestinationLocation())
                .negotiatedFare(bookingDTO.getNegotiatedFare())
                .status(BookingStatus.REQUESTED)
                .assignedDriverId(bookingDTO.getAssignedDriverId())
                .bookingTime(LocalDateTime.now())
                .deleted(false)
                .build();

        Booking saved = bookingRepository.save(booking);
        return convertToDTO(saved);
    }

    @Override
    public BookingDTO updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findByIdAndDeletedFalse(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        booking.setStatus(status);
        bookingRepository.save(booking);
        return convertToDTO(booking);
    }

    @Override
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAllByDeletedFalse().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingsByCustomerId(Long customerId) {
        return bookingRepository.findAllByDeletedFalse().stream()
                .filter(b -> b.getCustomer().getId().equals(customerId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingsByDriverId(Long driverId) {
        return bookingRepository.findAllByDeletedFalse().stream()
                .filter(b -> b.getDriver() != null && b.getDriver().getId().equals(driverId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        return convertToDTO(booking);
    }

    @Override
    public List<BookingDTO> getBookingHistoryByCustomer(Long customerId) {
        return getBookingsByCustomerId(customerId).stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingHistoryByDriver(Long driverId) {
        return getBookingsByDriverId(driverId).stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    @Override
    public String completeTrip(Long bookingId, Long driverId) {
        Booking booking = bookingRepository.findByIdAndDeletedFalse(bookingId)
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
        Booking booking = bookingRepository.findByIdAndDeletedFalse(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        return booking.getDropoffLocation();
    }

    @Override
    public Long getAssignedDriverId(Long bookingId) {
        Booking booking = bookingRepository.findByIdAndDeletedFalse(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        return booking.getAssignedDriverId();
    }

    @Override
    public List<BookingDTO> getOutsideStationBookings() {
        return bookingRepository.findAllByDeletedFalse().stream()
                .filter(b -> Boolean.TRUE.equals(b.getOutsideStation()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDTO updateNegotiatedFare(Long bookingId, Double newFare) {
        Booking booking = bookingRepository.findByIdAndDeletedFalse(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        booking.setNegotiatedFare(newFare);
        return convertToDTO(bookingRepository.save(booking));
    }

    @Override
    public Page<BookingDTO> getBookingsWithFilters(BookingStatus status, Boolean outsideStation,
                                                   Long customerId, Long driverId, String keyword,
                                                   Pageable pageable) {

        List<Booking> filtered = bookingRepository.findAllByDeletedFalse().stream()
                .filter(b -> status == null || b.getStatus() == status)
                .filter(b -> outsideStation == null || Boolean.TRUE.equals(b.getOutsideStation()) == outsideStation)
                .filter(b -> customerId == null || b.getCustomer().getId().equals(customerId))
                .filter(b -> driverId == null || (b.getDriver() != null && b.getDriver().getId().equals(driverId)))
                .filter(b -> keyword == null || keyword.isEmpty() ||
                        b.getPickupLocation().toLowerCase().contains(keyword.toLowerCase()) ||
                        b.getDropoffLocation().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<BookingDTO> pageContent = filtered.subList(start, end).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    @Override
    public void softDeleteBooking(Long bookingId) {
        Booking booking = bookingRepository.findByIdAndDeletedFalse(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        booking.setDeleted(true);
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    // Helper
    private BookingDTO convertToDTO(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .customerId(booking.getCustomer().getId())
                .driverId(booking.getDriver() != null ? booking.getDriver().getId() : null)
                .pickupLocation(booking.getPickupLocation())
                .dropoffLocation(booking.getDropoffLocation())
                .fare(booking.getFare())
                .outsideStation(booking.getOutsideStation())
                .sourceLocation(booking.getSourceLocation())
                .destinationLocation(booking.getDestinationLocation())
                .negotiatedFare(booking.getNegotiatedFare())
                .status(booking.getStatus())
                .assignedDriverId(booking.getAssignedDriverId())
                .build();
    }
}
