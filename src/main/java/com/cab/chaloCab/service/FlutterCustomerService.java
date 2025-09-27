package com.cab.chaloCab.service;

import com.cab.chaloCab.entity.Customer;

import java.util.List;
import java.util.Map;

public interface FlutterCustomerService {

    /**
     * Register a new customer (Name and Phone are mandatory).
     * @param customer The customer object containing name, phone, and optional details.
     * @return The saved customer entity.
     */
    Customer registerCustomer(Customer customer);

    /**
     * Get customer by phone number.
     * @param phone The phone number of the customer.
     * @return The customer entity if found.
     */
    Customer getCustomerByPhone(String phone);

    /**
     * Get customer by unique ID.
     * @param id The ID of the customer.
     * @return The customer entity.
     */
    Customer getCustomerById(Long id);

    /**
     * Get all active customers (not deleted).
     * @return List of active customers.
     */
    List<Customer> getAllActiveCustomers();

    /**
     * Deactivate a customer (admin flow).
     * @param id The ID of the customer to deactivate.
     */
    void deactivateCustomer(Long id);

    /**
     * Send OTP to a phone number.
     *
     * @param phoneNumber The phone number to which OTP will be sent.
     * @param role
     */
    Map<String, Object> sendOtp(String phoneNumber, String role);

    /**
     * Verify OTP and return JWT tokens (access + refresh).
     * @param phoneNumber The phone number of the customer.
     * @param otp The OTP entered by the user.
     * @return A map containing accessToken and refreshToken.
     */
    Map<String, String> verifyOtp(String phoneNumber, String otp, String requestedRole, String name, String deviceInfo);


    /**
     * Get the profile of a customer.
     * @param customerId The ID of the customer.
     * @return The customer entity.
     */
    Customer getProfile(Long customerId);

    /**
     * Update customer profile (name, email, address allowed; phone excluded).
     * @param customerId The ID of the customer.
     * @param updatedCustomer The updated customer details.
     * @return The updated customer entity.
     */
    Customer updateProfile(Long customerId, Customer updatedCustomer);

    /**
     * Deactivate account by the customer (soft delete).
     * @param customerId The ID of the customer.
     */
    void deactivateAccount(Long customerId);

    /**
     * Get the role of a user by phone number (for authentication/authorization).
     * @param phone The phone number of the user.
     * @return The role (e.g., CUSTOMER, DRIVER).
     */
    String getUserRole(String phone);

    // ---------------------------
    // New logout-related APIs
    // ---------------------------

    /**
     * Revoke a specific refresh token (preferred logout).
     * @param refreshToken the exact refresh token string issued to client
     */
    void logoutByRefreshToken(String refreshToken);

    /**
     * Revoke tokens for a given user for a particular device.
     * deviceInfo may be a Map or String depending on the controller payload.
     * The service will canonicalize/serialize it before matching.
     *
     * @param userId the user id (customer id)
     * @param deviceInfo object representing device info (Map or String)
     */
    void logoutByUserAndDevice(Long userId, Object deviceInfo);

    /**
     * Revoke all tokens for the given user (logout from all devices).
     * @param userId the user id (customer id)
     */
    void logoutAllDevices(Long userId);
}
