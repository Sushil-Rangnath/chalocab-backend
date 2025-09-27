package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.entity.Customer;
import com.cab.chaloCab.entity.Driver;
import com.cab.chaloCab.entity.DriverRequest;
import com.cab.chaloCab.enums.DriverRequestStatus;
import com.cab.chaloCab.enums.Role;
import com.cab.chaloCab.repository.CustomerRepository;
import com.cab.chaloCab.repository.DriverRepository;
import com.cab.chaloCab.repository.DriverRequestRepository;
import com.cab.chaloCab.service.FlutterCustomerService;
import com.cab.chaloCab.service.RefreshTokenService;
import com.cab.chaloCab.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FlutterCustomerServiceImpl implements FlutterCustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverRequestRepository driverRequestRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Inject the refresh token service so mobile-issued refresh tokens are persisted
    @Autowired
    private RefreshTokenService refreshTokenService;

    // ObjectMapper for canonicalizing deviceInfo objects to JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    // OTP entry with code + expiry time
    private static class OtpEntry {
        final String code;
        final long expiresAtEpochSeconds;
        OtpEntry(String code, long expiresAtEpochSeconds) {
            this.code = code;
            this.expiresAtEpochSeconds = expiresAtEpochSeconds;
        }
    }

    // Thread-safe in-memory store for OTPs keyed by phone number (stores code + expiry)
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    // OTP settings
    private static final int OTP_LENGTH = 4;
    private static final int OTP_TTL_MINUTES = 10;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String OTP_CHARS = "0123456789"; // only digits

    // Message constants (keep stable so frontend can rely on exact strings)
    private static final String MSG_OTP_GENERATED = "OTP generated";
    private static final String MSG_INVALID_OTP = "Invalid OTP";
    private static final String MSG_OTP_EXPIRED = "OTP expired";
    private static final String MSG_SAVED_SUCCESS = "Saved Successfully";
    private static final String MSG_PENDING = "PENDING";
    private static final String NEW_DRIVER_REQ = "NEW";
    private static final String MSG_WELCOME = "WELCOME";
    private static final String MSG_INVALID_ROLE = "Invalid role";
    private static final String MSG_PHONE_REQUIRED = "phoneNumber is required";
    private static final String MSG_ACCOUNT_INACTIVE = "This account is inactive. Contact support.";
    private static final String MSG_DRIVER_REQUEST_REJECTED = "Driver Request Rejected.";
    private static final String MSG_DRIVER_PENDING_STATUS = "Driver Request is in pending status.";

    /**
     * Generate numeric OTP (4 digits), store in in-memory map and return OTP+expiry to caller.
     * Returns a map with keys: message, phone, otp (dev), expiresInMinutes
     */
    @Override
    public Map<String, Object> sendOtp(String phoneNumber, String role) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MSG_PHONE_REQUIRED);
        }

        // CUSTOMER checks
        if ("CUSTOMER".equalsIgnoreCase(role)) {
            Optional<Customer> existingCustomer = customerRepository.findByPhone(phoneNumber);
            if (existingCustomer.isPresent() && !existingCustomer.get().isActive()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, MSG_ACCOUNT_INACTIVE);
            }
        } else if ("DRIVER".equalsIgnoreCase(role)) {
            // If there's an existing driver request, return PENDING + request id (do not block)
            Optional<DriverRequest> existingDriverReq = driverRequestRepository.findByPhoneNumber(phoneNumber);
            if (existingDriverReq.isPresent()) {
                DriverRequest dr = existingDriverReq.get();
                Map<String, Object> resp = new HashMap<>();
                resp.put("message", MSG_PENDING);
                resp.put("driverRequestId", dr.getId());
                // include any fields saved earlier so frontend can prefill the form:
                resp.put("name", dr.getName());
                resp.put("phone", dr.getPhoneNumber());
                resp.put("vehicleRegistration", dr.getVehicleNumber());
                resp.put("licenseNumber", dr.getLicenseNumber());
                resp.put("vehicleType", dr.getVehicleType());
                resp.put("expiresInMinutes", OTP_TTL_MINUTES);
                return resp;
            } else {
                // If driver exists in driver table and is REJECTED -> block
                Optional<Driver> driver = driverRepository.findByPhoneNumber(phoneNumber);
                if (driver.isPresent()) {
                    try {
                        if (driver.get().getStatus() != null && "REJECTED".equalsIgnoreCase(driver.get().getStatus().name())) {
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MSG_DRIVER_REQUEST_REJECTED);
                        }
                    } catch (Exception ignore) {
                        if ("REJECTED".equalsIgnoreCase(String.valueOf(driver.get().getStatus()))) {
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MSG_DRIVER_REQUEST_REJECTED);
                        }
                    }
                }
            }
        }

        // Generate secure numeric OTP of length OTP_LENGTH
        StringBuilder sb = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            int idx = secureRandom.nextInt(OTP_CHARS.length());
            sb.append(OTP_CHARS.charAt(idx));
        }
        String otp = sb.toString();

        // Compute expiry epoch seconds
        long expiresAt = Instant.now().plusSeconds(OTP_TTL_MINUTES * 60L).getEpochSecond();

        // Store in in-memory map
        otpStore.put(phoneNumber, new OtpEntry(otp, expiresAt));

        // DEV: log OTP only in non-production (guarded by env var)
        if (Objects.equals(System.getProperty("app.env"), "dev") || Objects.equals(System.getenv("APP_ENV"), "dev")) {
            System.out.println("Generated OTP for " + phoneNumber + " = " + otp);
        }

        // Prepare response map including otp and expiry minutes (frontend will manage countdown)
        Map<String, Object> response = new HashMap<>();
        response.put("message", MSG_OTP_GENERATED);
        response.put("phone", phoneNumber);
        // keeping otp handy for dev - remove in production if undesired
        response.put("otp", otp);
        response.put("expiresInMinutes", OTP_TTL_MINUTES);

        return response;
    }


    /**
     * Verify OTP stored in in-memory map. On success, remove it and proceed with login/registration.
     * Returns tokens/info in the same shape as before.
     *
     * Behavior:
     * - Validates OTP and expiry.
     * - For CUSTOMER: create customer if missing and return tokens.
     * - For DRIVER:
     *    * if pending DriverRequest exists -> return PENDING + driverRequestId + saved fields
     *    * else if approved Driver exists -> return WELCOME + tokens
     *    * else create new DriverRequest (PENDING) and return PENDING + driverRequestId
     *
     * NOTE: deviceInfo may be null. It's stored along with refresh token for per-device sessions.
     */
    @Override
    public Map<String, String> verifyOtp(String phoneNumber, String otp, String requestedRole, String name, String deviceInfo) {
        if (phoneNumber == null || phoneNumber.isBlank() || otp == null || otp.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "phoneNumber and otp required");
        }

        // Validate OTP exists
        OtpEntry entry = otpStore.get(phoneNumber);
        if (entry == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MSG_INVALID_OTP);
        }

        // Check expiry
        long now = Instant.now().getEpochSecond();
        if (entry.expiresAtEpochSeconds < now) {
            otpStore.remove(phoneNumber);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MSG_OTP_EXPIRED);
        }

        // Match code
        if (!entry.code.equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MSG_INVALID_OTP);
        }

        // Remove OTP (single-use)
        otpStore.remove(phoneNumber);

        Map<String, String> response = new HashMap<>();

        if ("CUSTOMER".equalsIgnoreCase(requestedRole)) {
            Customer customer = customerRepository.findByPhone(phoneNumber)
                    .orElseGet(() -> {
                        Customer newCustomer = new Customer();
                        newCustomer.setPhone(phoneNumber);
                        newCustomer.setName(name);
                        newCustomer.setRole(Role.CUSTOMER);
                        newCustomer.setActive(true);
                        return customerRepository.save(newCustomer);
                    });

            String accessToken = jwtUtil.generateToken(phoneNumber, Role.CUSTOMER.name());
            String refreshToken = jwtUtil.generateRefreshToken(phoneNumber, Role.CUSTOMER.name());

            // persist refresh token (hash stored by RefreshTokenService)
            Date refreshExpDate = jwtUtil.extractClaim(refreshToken, Claims::getExpiration);
            Instant refreshExpiresAt = refreshExpDate != null ? refreshExpDate.toInstant() : null;
            if (refreshExpiresAt != null) {
                refreshTokenService.createRefreshToken(String.valueOf(customer.getId()), refreshToken, refreshExpiresAt, deviceInfo);
            }

            response.put("message", MSG_SAVED_SUCCESS);
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("role", Role.CUSTOMER.name());
            response.put("userId", String.valueOf(customer.getId()));

        } else if ("DRIVER".equalsIgnoreCase(requestedRole)) {
            Optional<DriverRequest> requestedDriver = driverRequestRepository.findByPhoneNumberAndStatus(phoneNumber, DriverRequestStatus.PENDING);
            Optional<Driver> driverObj = driverRepository.findByPhoneNumber(phoneNumber);

            if (requestedDriver.isPresent()) {
                // If there is a pending driver request, return PENDING + its id and saved fields
                DriverRequest dr = requestedDriver.get();
                response.put("message", MSG_PENDING);
                response.put("driverRequestId", dr.getId() != null ? dr.getId().toString() : "");
                response.put("name", dr.getName() != null ? dr.getName() : "");
                response.put("phone", dr.getPhoneNumber() != null ? dr.getPhoneNumber() : "");
                response.put("vehicleRegistration", dr.getVehicleNumber() != null ? dr.getVehicleNumber() : "");
                response.put("licenseNumber", dr.getLicenseNumber() != null ? dr.getLicenseNumber() : "");
                response.put("vehicleType", dr.getVehicleType() != null ? dr.getVehicleType() : "");
                response.put("accessToken", "");
                response.put("refreshToken", "");
                response.put("role", Role.DRIVER.name());
            } else if (driverObj.isPresent()) {
                // Driver already approved → allow login
                Driver driver = driverObj.get();
                String accessToken = jwtUtil.generateToken(phoneNumber, Role.DRIVER.name());
                String refreshToken = jwtUtil.generateRefreshToken(phoneNumber, Role.DRIVER.name());

                // persist refresh token for driver
                Date refreshExpDate = jwtUtil.extractClaim(refreshToken, Claims::getExpiration);
                Instant refreshExpiresAt = refreshExpDate != null ? refreshExpDate.toInstant() : null;
                if (refreshExpiresAt != null) {
                    refreshTokenService.createRefreshToken(String.valueOf(driver.getId()), refreshToken, refreshExpiresAt, deviceInfo);
                }

                response.put("message", MSG_WELCOME);
                response.put("accessToken", accessToken);
                response.put("refreshToken", refreshToken);
                response.put("role", Role.DRIVER.name());
                response.put("userId", String.valueOf(driver.getId()));
            } else {
                // New driver request submission: create a DB row with PENDING and return its id
                DriverRequest newRequest = new DriverRequest();
                newRequest.setPhoneNumber(phoneNumber);
                newRequest.setName(name); // Flutter will update more details later
               // newRequest.setStatus(DriverRequestStatus.PENDING);
                // Optionally persist newRequest if you want to capture the request immediately:
                // DriverRequest saved = driverRequestRepository.save(newRequest);
                // response.put("driverRequestId", saved.getId() != null ? saved.getId().toString() : "");

                response.put("message", NEW_DRIVER_REQ);
                response.put("accessToken", "");
                response.put("refreshToken", "");
                response.put("role", Role.DRIVER.name());
            }

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MSG_INVALID_ROLE);
        }

        return response;
    }

    @Override
    public Customer registerCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer getCustomerByPhone(String phone) {
        return customerRepository.findByPhone(phone)
                .orElse(null); // returns null if not found
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public List<Customer> getAllActiveCustomers() {
        return customerRepository.findByActiveTrue();
    }

    @Override
    public void deactivateCustomer(Long id) {
        Customer customer = getCustomerById(id);
        if (customer != null) {
            customer.setActive(false);
            customerRepository.save(customer);
        }
    }

    @Override
    public Customer getProfile(Long customerId) {
        return getCustomerById(customerId);
    }

    @Override
    public Customer updateProfile(Long customerId, Customer updatedCustomer) {
        Customer existing = getCustomerById(customerId);
        if (existing != null) {
            existing.setName(updatedCustomer.getName());
            existing.setEmail(updatedCustomer.getEmail());
            existing.setAddress(updatedCustomer.getAddress());
            return customerRepository.save(existing);
        }
        return null;
    }

    @Override
    public void deactivateAccount(Long customerId) {
        deactivateCustomer(customerId);
    }

    @Override
    public String getUserRole(String phone) {
        return customerRepository.findByPhone(phone)
                .map(customer -> customer.getRole().name())
                .orElse("CUSTOMER");
    }

    // -------------------------
    // New logout implementations
    // -------------------------

    @Override
    public void logoutByRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "refreshToken must not be empty");
        }
        // Delegate to RefreshTokenService - revoke the exact token
        refreshTokenService.revokeToken(refreshToken);
    }

    @Override
    public void logoutByUserAndDevice(Long userId, Object deviceInfo) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId required");
        }
        String deviceInfoJson = null;
        try {
            if (deviceInfo == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "deviceInfo required for this path");
            }
            // Canonicalize deviceInfo to JSON string for matching/storage
            if (deviceInfo instanceof String) {
                deviceInfoJson = (String) deviceInfo;
            } else {
                deviceInfoJson = objectMapper.writeValueAsString(deviceInfo);
            }
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            // fallback to toString if serialization fails
            deviceInfoJson = Objects.toString(deviceInfo, null);
            if (deviceInfoJson == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid deviceInfo");
            }
        }

        refreshTokenService.revokeTokensForUserByDevice(userId.toString(), deviceInfoJson);
    }

    @Override
    public void logoutAllDevices(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId required");
        }
        refreshTokenService.revokeAllTokensForUser(userId.toString());
    }
}
