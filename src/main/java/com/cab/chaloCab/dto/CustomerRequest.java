package com.cab.chaloCab.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {
    private String name;
    private String email;
    private String phone;
    private String address;
}
