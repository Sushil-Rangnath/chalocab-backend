package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.Role;

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;

    private String phoneNumber; // ✅ mandatory
    private String licenseNumber;   // for drivers only
    private String vehicleNumber;   // for drivers only
    private String address;         // for drivers only

    public RegisterRequest() {}

    public RegisterRequest(String name, String email, String password, Role role, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    // getters / setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
