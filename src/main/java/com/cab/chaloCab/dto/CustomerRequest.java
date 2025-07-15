package com.cab.chaloCab.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
    private String name;
    private String email;
    private String phone;
    private String address;
}
