package com.rvk.skycommerce.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProfessionalClientModel {
    String id;
    String companyName;
    String registrationNumber;
    BigDecimal annualRevenue;
    String vatNumber;
}
