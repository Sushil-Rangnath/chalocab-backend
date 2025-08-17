package com.cab.chaloCab.entity;

import com.cab.chaloCab.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Column(name = "pickup_location")
    private String pickupLocation;

    @Column(name = "drop_location")
    private String dropoffLocation;

    @Column(name = "fare")
    private Double fare;

    @Column(name = "booking_time")
    private LocalDateTime bookingTime;

    @Column(name = "assigned_driver_id")
    private Long assignedDriverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;

    @Column(nullable = true)
    private String sourceLocation;

    @Column(nullable = true)
    private String destinationLocation;

    @Column(nullable = true)
    private Boolean outsideStation;

    @Column(nullable = true)
    private Double negotiatedFare;

    @Column(nullable = false)
    private boolean deleted = false;

    // Utility getters for DTO mapping
    public Long getCustomerId() {
        return customer != null ? customer.getId() : null;
    }

    public Long getDriverId() {
        return driver != null ? driver.getId() : null;
    }

    public void setCustomerId(Long customerId) {
        if (this.customer == null) this.customer = new Customer();
        this.customer.setId(customerId);
    }

    public void setDriverId(Long driverId) {
        if (this.driver == null) this.driver = new Driver();
        this.driver.setId(driverId);
    }
}
