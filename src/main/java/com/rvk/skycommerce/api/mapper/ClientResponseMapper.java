package com.rvk.skycommerce.api.mapper;

import com.rvk.skycommerce.api.dto.client.IndividualClientResponse;
import com.rvk.skycommerce.api.dto.client.ProfessionalClientResponse;
import com.rvk.skycommerce.model.IndividualClientModel;
import com.rvk.skycommerce.model.ProfessionalClientModel;

public class ClientResponseMapper {

    public static IndividualClientResponse toResponse(IndividualClientModel model) {
        return IndividualClientResponse.builder()
                .id(model.getId())
                .firstName(model.getFirstName())
                .lastName(model.getLastName())
                .build();
    }

    public static ProfessionalClientResponse toResponse(ProfessionalClientModel model) {
        return ProfessionalClientResponse.builder()
                .id(model.getId())
                .companyName(model.getCompanyName())
                .registrationNumber(model.getRegistrationNumber())
                .annualRevenue(model.getAnnualRevenue())
                .vatNumber(model.getVatNumber())
                .build();
    }
}
