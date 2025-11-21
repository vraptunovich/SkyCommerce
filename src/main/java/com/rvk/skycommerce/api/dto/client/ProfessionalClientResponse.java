package com.rvk.skycommerce.api.dto.client;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProfessionalClientResponse {
    String id;
    String companyName;
    String registrationNumber;
    BigDecimal annualRevenue;
    String vatNumber;
}
