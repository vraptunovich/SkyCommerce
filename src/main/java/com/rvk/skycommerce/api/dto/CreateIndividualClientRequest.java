package com.rvk.skycommerce.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateIndividualClientRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
}
