package com.rvk.skycommerce.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProfessionalClientRequest {

    @NotBlank
    private String companyName;

    @NotBlank
    private String registrationNumber;

    @NotNull
    @PositiveOrZero
    private BigDecimal annualRevenue;

    private String vatNumber;
}
