package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.FlutterAuthResponse;
import com.cab.chaloCab.entity.Customer;
import com.cab.chaloCab.service.FlutterCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * FlutterCustomerController
 *
 * - Keeps existing OTP endpoints (send-otp, verify-otp) unchanged in behavior.
 * - Accepts optional deviceInfo in verify-otp request and forwards to service.
 */
@RestController
@RequestMapping("/api/flutter/customer")
public class FlutterCustomerController {

    @Autowired
    private FlutterCustomerService customerService;

    // -----------------------------
    // Existing OTP endpoints (unchanged behavior)
    // -----------------------------
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String phone = request.get("phoneNumber");
        String role = request.get("requestedRole");

        // Basic null/blank check
        if (phone == null || phone.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "phoneNumber is required");
        }

        // Indian mobile number validation (exactly 10 digits, all numeric)
        if (!phone.matches("^\\d{10}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide a valid 10-digit Indian phone number");
        }

        // Service returns Map<String,Object> with keys: message, phone, otp, expiresInMinutes
        Map<String, Object> result = customerService.sendOtp(phone, role);
        return ResponseEntity.ok(result);
    }


    /**
     * verify-otp request body now may include:
     * {
     *   "phoneNumber": "9876543210",
     *   "otp": "1234",
     *   "requestedRole": "CUSTOMER",
     *   "name": "Sushil",
     *   "deviceInfo": "android;Rydo/1.0.0;deviceId:abc123"   // optional
     * }
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<FlutterAuthResponse> verifyOtp(@RequestBody Map<String, String> request) {
        String phone = request.get("phoneNumber");
        String otp = request.get("otp");
        String role = request.get("requestedRole");
        String name = request.get("name");
        String deviceInfo = request.get("deviceInfo"); // optional; may be null

        try {
            // NOTE: service signature must accept deviceInfo as last parameter:
            // Map<String,String> tokens = customerService.verifyOtp(phone, otp, role, name, deviceInfo);
            Map<String, String> tokens = customerService.verifyOtp(phone, otp, role, name, deviceInfo);

            FlutterAuthResponse response = new FlutterAuthResponse(
                    tokens.getOrDefault("message", "OK"),
                    tokens.get("accessToken"),
                    tokens.get("refreshToken"),
                    tokens.get("role"),
                    tokens.get("userId")
            );

            return ResponseEntity.ok(response);

        } catch (ResponseStatusException ex) {
            // Return body + X-Error-Message header
            FlutterAuthResponse response = new FlutterAuthResponse(
                    ex.getReason(), null, null, null, null
            );
            return responseWithError(response, ex);

        } catch (Exception e) {
            FlutterAuthResponse response = new FlutterAuthResponse(
                    "Internal server error", null, null, null, null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // -----------------------------
    // NEW: Logout endpoint
    // -----------------------------
    /**
     * POST /api/flutter/customer/logout
     *
     * Accepts body:
     *  - {"refreshToken":"..."}  (preferred)
     *  - {"userId":123, "deviceInfo": { ... }}  (optional device)
     *  - {"userId":123}  (revoke all)
     *
     * Delegates actual DB logic to FlutterCustomerService.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, Object> body) {
        try {
            if (body == null || body.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body required");
            }

            // Preferred path: revoke by refresh token
            if (body.containsKey("refreshToken")) {
                Object rtObj = body.get("refreshToken");
                if (rtObj == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "refreshToken must not be null");
                }
                String refreshToken = rtObj.toString().trim();
                if (refreshToken.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "refreshToken must not be empty");
                }

                // Delegate to service (implement this method)
                customerService.logoutByRefreshToken(refreshToken);
                return ResponseEntity.ok(Map.of("message", "Logged out (token revoked)"));
            }

            // Fallback: handle by userId + deviceInfo or userId only
            if (body.containsKey("userId")) {
                Object uidObj = body.get("userId");
                if (uidObj == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId must not be null");
                }
                Long userId;
                try {
                    userId = Long.valueOf(uidObj.toString());
                } catch (NumberFormatException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId must be numeric");
                }

                if (body.containsKey("deviceInfo")) {
                    Object deviceInfoObj = body.get("deviceInfo");
                    // Pass raw object to service - service can canonicalize JSON or compute fingerprint
                    customerService.logoutByUserAndDevice(userId, deviceInfoObj);
                    return ResponseEntity.ok(Map.of("message", "Logged out from device"));
                } else {
                    customerService.logoutAllDevices(userId);
                    return ResponseEntity.ok(Map.of("message", "Logged out from all devices"));
                }
            }

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide refreshToken or userId");

        } catch (ResponseStatusException ex) {
            return responseWithError(Map.of("message", ex.getReason()), ex);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Internal server error"));
        }
    }

    // -----------------------------
    // Profile endpoints
    // -----------------------------

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable("id") Long id) {
        try {
            Customer customer = customerService.getProfile(id);
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Customer not found"));
            }
            return ResponseEntity.ok(customer);
        } catch (ResponseStatusException ex) {
            return responseWithError(Map.of("message", ex.getReason()), ex);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Internal server error"));
        }
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable("id") Long id, @RequestBody Customer updated) {
        try {
            String email = updated.getEmail();
            if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Please provide a valid email"));
            }

            Customer result = customerService.updateProfile(id, updated);
            return ResponseEntity.ok(result);

        } catch (ResponseStatusException ex) {
            return responseWithError(Map.of("message", ex.getReason()), ex);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }
    }

    @DeleteMapping("/profile/{id}")
    public ResponseEntity<?> deactivateAccount(@PathVariable("id") Long id) {
        try {
            customerService.deactivateAccount(id);
            return ResponseEntity.ok(Map.of("message", "Account deactivated"));
        } catch (ResponseStatusException ex) {
            return responseWithError(Map.of("message", ex.getReason()), ex);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Internal server error"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllActiveCustomers() {
        try {
            List<Customer> list = customerService.getAllActiveCustomers();
            return ResponseEntity.ok(list);
        } catch (ResponseStatusException ex) {
            return responseWithError(Map.of("message", ex.getReason()), ex);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Internal server error"));
        }
    }

    @DeleteMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivateCustomer(@PathVariable("id") Long id) {
        try {
            customerService.deactivateCustomer(id);
            return ResponseEntity.ok(Map.of("message", "Customer deactivated"));
        } catch (ResponseStatusException ex) {
            return responseWithError(Map.of("message", ex.getReason()), ex);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Internal server error"));
        }
    }

    @GetMapping("/role")
    public ResponseEntity<?> getUserRole(@RequestParam("phone") String phone) {
        try {
            String role = customerService.getUserRole(phone);
            if (role == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
            }
            return ResponseEntity.ok(Map.of("phone", phone, "role", role));
        } catch (ResponseStatusException ex) {
            return responseWithError(Map.of("message", ex.getReason()), ex);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Internal server error"));
        }
    }

    private <T> ResponseEntity<T> responseWithError(T body, ResponseStatusException ex) {
        String reason = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Error-Message", reason);
        return new ResponseEntity<>(body, headers, ex.getStatusCode());
    }
}
