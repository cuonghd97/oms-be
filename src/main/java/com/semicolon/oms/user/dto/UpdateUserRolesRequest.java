package com.semicolon.oms.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRolesRequest {
    @NotEmpty(message = "Roles must not be empty")
    private Set<String> roles;
}
