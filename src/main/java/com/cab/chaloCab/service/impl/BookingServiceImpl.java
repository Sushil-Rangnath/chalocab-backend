package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.BookingDTO;
import com.cab.chaloCab.entity.Booking;
import com.cab.chaloCab.entity.Fare;
import com.cab.chaloCab.enums.BookingStatus;
import com.cab.chaloCab.enums.BookingType;
import com.cab.chaloCab.repository.BookingRepository;
import com.cab.chaloCab.repository.CustomerRepository;
import com.cab.chaloCab.repository.DriverRepository;
import com.cab.chaloCab.repository.FareRepository;
import com.cab.chaloCab.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final FareRepository fareRepository;

    @Override
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        log.info("BookingService.createBooking called with DTO: {}", bookingDTO);

        Booking booking = mapToEntity(bookingDTO);

        // 🔹 Validate cabType
        if (booking.getCabType() == null) {
            log.warn("createBooking -> cabType is null for DTO: {}", bookingDTO);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cab type is required");
        }

        // 🔹 Fetch fare from table
        // 🔹 Fetch fare from table
        Fare fare = fareRepository.findByCabTypeAndDeletedFalse(booking.getCabType())
                .orElseThrow(() -> {
                    log.warn("No fare configured for cabType {}", booking.getCabType());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Fare not set for cab type");
                });

        log.info("Found fare for {}: baseFare={}, perKm={}", fare.getCabType(), fare.getBaseFare(), fare.getPerKmFare());

        // 🔹 Calculate fare if distance provided
        if (bookingDTO.getDistanceKm() != null) {
            double estimatedFare = fare.getBaseFare() + (fare.getPerKmFare() * bookingDTO.getDistanceKm());
            booking.setFare(estimatedFare);
            log.info("Calculated fare: distanceKm={}, estimatedFare={}", bookingDTO.getDistanceKm(), estimatedFare);
        }

        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(booking.getStatus() != null ? booking.getStatus() : BookingStatus.REQUESTED);

        try {
            Booking saved = bookingRepository.save(booking);
            log.info("Booking saved with id={}", saved.getId());
            return mapToDTO(saved);
        } catch (Exception ex) {
            log.error("Error saving booking for customerId={}: {}", booking.getCustomerId(), ex.toString(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save booking");
        }
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
        return bookingRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingsByDriverId(Long driverId) {
        return bookingRepository.findByDriverId(driverId)
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

        // update fields (preserve mapping used earlier)
        booking.setPickupLocation(bookingDTO.getPickupLocation());
        booking.setDropLocation(bookingDTO.getDropLocation());
        booking.setFromCity(bookingDTO.getFromCity());
        booking.setToCity(bookingDTO.getToCity());
        booking.setTravelDate(bookingDTO.getTravelDate());
        booking.setCabType(bookingDTO.getCabType());
        booking.setAdditionalDetails(bookingDTO.getAdditionalDetails());
        booking.setRentalPackage(bookingDTO.getRentalPackage());
        booking.setFare(bookingDTO.getFare());
        booking.setNegotiatedFare(bookingDTO.getNegotiatedFare());
        booking.setBookingType(bookingDTO.getBookingType());
        booking.setStatus(bookingDTO.getStatus());
        booking.setAssignedDriverId(bookingDTO.getAssignedDriverId());
        booking.setDriverId(bookingDTO.getDriverId());
        booking.setCustomerId(bookingDTO.getCustomerId());

        Booking updated = bookingRepository.save(booking);
        return mapToDTO(updated);
    }

    @Override
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        booking.setDeleted(true);
        bookingRepository.save(booking);
    }

    @Override
    public List<BookingDTO> getOutsideStationBookings() {
        return bookingRepository.findByBookingType(BookingType.OUTSTATION)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public Double calculateTotalRevenue() {
        return bookingRepository.findAll().stream()
                .filter(b -> !b.isDeleted())
                .mapToDouble(b -> {
                    if (b.getNegotiatedFare() != null) return b.getNegotiatedFare();
                    return b.getFare() != null ? b.getFare() : 0.0;
                })
                .sum();
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
        Page<Booking> bookingsPage = bookingRepository.findByCustomerIdAndDeletedFalse(customerId, pageable);
        return bookingsPage.stream().map(this::mapToDTO).toList();
    }

    @Override
    public List<BookingDTO> getBookingHistoryByDriver(Long driverId, Pageable pageable) {
        Page<Booking> bookingsPage = bookingRepository.findByDriverIdAndDeletedFalse(driverId, pageable);
        return bookingsPage.stream().map(this::mapToDTO).toList();
    }

    @Override
    public Page<Booking> getAllBookings(Pageable pageable) {
        return bookingRepository.findByDeletedFalse(pageable);
    }

    // ---------------------- Mapper Utils ----------------------
    private BookingDTO mapToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setCustomerId(booking.getCustomerId());
        dto.setDriverId(booking.getDriverId());
        dto.setAssignedDriverId(booking.getAssignedDriverId());
        dto.setDistanceKm(booking.getDistanceKm());

        dto.setBookingType(booking.getBookingType());
        dto.setPickupLocation(booking.getPickupLocation());
        dto.setDropLocation(booking.getDropLocation());
        dto.setFromCity(booking.getFromCity());
        dto.setToCity(booking.getToCity());
        dto.setTravelDate(booking.getTravelDate());
        dto.setAdditionalDetails(booking.getAdditionalDetails());
        dto.setCabType(booking.getCabType());
        dto.setRentalPackage(booking.getRentalPackage());
        dto.setFare(booking.getFare());
        dto.setNegotiatedFare(booking.getNegotiatedFare());
        dto.setBookingTime(booking.getBookingTime());
        dto.setStatus(booking.getStatus());
        dto.setDeleted(booking.isDeleted());

        return dto;
    }

    private Booking mapToEntity(BookingDTO dto) {
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setCustomerId(dto.getCustomerId());
        booking.setDriverId(dto.getDriverId());
        booking.setAssignedDriverId(dto.getAssignedDriverId());

        booking.setBookingType(dto.getBookingType());
        booking.setPickupLocation(dto.getPickupLocation());
        booking.setDropLocation(dto.getDropLocation());
        booking.setFromCity(dto.getFromCity());
        booking.setToCity(dto.getToCity());
        booking.setDistanceKm(dto.getDistanceKm());
        booking.setTravelDate(dto.getTravelDate());
        booking.setAdditionalDetails(dto.getAdditionalDetails());

        // <-- IMPORTANT: map cabType from DTO to entity (was missing)
        booking.setCabType(dto.getCabType());

        booking.setRentalPackage(dto.getRentalPackage());
        booking.setFare(dto.getFare());
        booking.setNegotiatedFare(dto.getNegotiatedFare());
        booking.setBookingTime(dto.getBookingTime());
        booking.setStatus(dto.getStatus() != null ? dto.getStatus() : BookingStatus.REQUESTED);

        return booking;
    }

}
