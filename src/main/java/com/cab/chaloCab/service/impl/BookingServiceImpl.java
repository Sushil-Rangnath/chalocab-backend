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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;

    @Override
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        Booking booking = mapToEntity(bookingDTO);
        Booking saved = bookingRepository.save(booking);
        return mapToDTO(saved);
    }

    @Override
    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        return mapToDTO(booking);
    }

    @Override
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingsByCustomerId(Long customerId) {
        return bookingRepository.findByCustomer_Id(customerId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingsByDriverId(Long driverId) {
        return bookingRepository.findByDriver_Id(driverId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingsByAssignedDriverId(Long driverId) {
        return bookingRepository.findByAssignedDriverId(driverId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDTO updateBooking(Long id, BookingDTO bookingDTO) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        booking.setPickupLocation(bookingDTO.getPickupLocation());
        booking.setDropoffLocation(bookingDTO.getDropLocation());
        booking.setSourceLocation(bookingDTO.getSourceLocation());
        booking.setDestinationLocation(bookingDTO.getDestinationLocation());
        booking.setOutsideStation(bookingDTO.isOutsideStation());
        booking.setFare(bookingDTO.getFare());
        booking.setNegotiatedFare(bookingDTO.getNegotiatedFare());
        booking.setStatus(bookingDTO.getStatus());
        booking.setAssignedDriverId(bookingDTO.getAssignedDriverId());

        if (bookingDTO.getDriverId() != null) {
            Driver driver = driverRepository.findById(bookingDTO.getDriverId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));
            booking.setDriver(driver);
        }

        Booking updated = bookingRepository.save(booking);
        return mapToDTO(updated);
    }


    @Override
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        bookingRepository.delete(booking);
    }

    @Override
    public List<BookingDTO> getOutsideStationBookings() {
        return bookingRepository.findByOutsideStationTrue()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Double calculateTotalRevenue() {
        return bookingRepository.findAll()
                .stream()
                .mapToDouble(b -> b.getNegotiatedFare() != null ? b.getNegotiatedFare() : b.getFare())
                .sum();
    }

    // Utility methods
    private BookingDTO mapToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setCustomerId(booking.getCustomerId());
        dto.setDriverId(booking.getDriverId());
        dto.setAssignedDriverId(booking.getAssignedDriverId());
        dto.setPickupLocation(booking.getPickupLocation());
        dto.setDropLocation(booking.getDropoffLocation());
        dto.setSourceLocation(booking.getSourceLocation());
        dto.setDestinationLocation(booking.getDestinationLocation());
        dto.setOutsideStation(booking.getOutsideStation());
        dto.setFare(booking.getFare());
        dto.setNegotiatedFare(booking.getNegotiatedFare());
        dto.setStatus(booking.getStatus());

        // Populate customer info
        if (booking.getCustomerId() != null) {
            customerRepository.findById(booking.getCustomerId()).ifPresent(c -> {
                dto.setCustomerName(c.getName());
                dto.setCustomerPhone(c.getPhone());
            });
        }

        // Populate driver info
        if (booking.getDriverId() != null) {
            driverRepository.findById(booking.getDriverId()).ifPresent(d -> {
                dto.setDriverName(d.getName());
                dto.setDriverPhone(d.getPhoneNumber());
            });
        }

        return dto;
    }

    private Booking mapToEntity(BookingDTO dto) {
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setCustomerId(dto.getCustomerId());
        booking.setDriverId(dto.getDriverId());
        booking.setAssignedDriverId(dto.getAssignedDriverId());
        booking.setPickupLocation(dto.getPickupLocation());
        booking.setOutsideStation(dto.isOutsideStation());
        booking.setSourceLocation(dto.getSourceLocation());
        booking.setDestinationLocation(dto.getDestinationLocation());
        booking.setOutsideStation(dto.isOutsideStation());
        booking.setFare(dto.getFare());
        booking.setNegotiatedFare(dto.getNegotiatedFare());
        booking.setStatus(dto.getStatus() != null ? dto.getStatus() : BookingStatus.REQUESTED);
        return booking;
    }

    @Override
    public BookingDTO completeTrip(Long bookingId, Long driverId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getAssignedDriverId() == null || !booking.getAssignedDriverId().equals(driverId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver not assigned to this booking");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        Booking updated = bookingRepository.save(booking);
        return mapToDTO(updated);
    }

    @Override
    public List<BookingDTO> getBookingHistoryByCustomer(Long customerId, Pageable pageable) {
        Page<Booking> bookingsPage = bookingRepository.findByCustomer_IdAndDeletedFalse(customerId, pageable);
        return bookingsPage.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<BookingDTO> getBookingHistoryByDriver(Long driverId, Pageable pageable) {
        Page<Booking> bookingsPage = bookingRepository.findByDriver_IdAndDeletedFalse(driverId, pageable);
        return bookingsPage.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public Page<Booking> getAllBookings(Pageable pageable) {
        return bookingRepository.findByDeletedFalse(pageable);
    }


    @Override
    public List<BookingDTO> getBookingHistoryByCustomer(Long customerId) {
        List<Booking> bookings = bookingRepository.findByCustomer_IdAndDeletedFalse(customerId);
        return bookings.stream().map(this::mapToDTO).toList();
    }

    @Override
    public List<BookingDTO> getBookingHistoryByDriver(Long driverId) {
        List<Booking> bookings = bookingRepository.findByDriver_IdAndDeletedFalse(driverId);
        return bookings.stream().map(this::mapToDTO).toList();
    }



}
