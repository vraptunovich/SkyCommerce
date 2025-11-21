package com.rvk.skycommerce.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("PROFESSIONAL")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfessionalClient extends Client {

    @Column(name = "company_name", length = 200, nullable = false)
    private String companyName;

    @Column(name = "vat_number", length = 50)
    private String vatNumber;

    @Column(name = "registration_number", length = 50, nullable = false)
    private String registrationNumber;

    @Column(name = "annual_revenue", precision = 19, scale = 2, nullable = false)
    private BigDecimal annualRevenue;

    public ProfessionalClient(
            String id,
            String companyName,
            String registrationNumber,
            BigDecimal annualRevenue,
            String vatNumber
    ) {
        super(id);
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.annualRevenue = annualRevenue;
        this.vatNumber = vatNumber;
    }
}
