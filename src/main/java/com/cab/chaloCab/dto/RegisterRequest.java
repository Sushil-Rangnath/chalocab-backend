package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role; // ✅ Enum
}
