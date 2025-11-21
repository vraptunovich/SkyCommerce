package com.rvk.skycommerce.api.dto.client;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateIndividualClientRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
}
