package com.cab.chaloCab.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
}
