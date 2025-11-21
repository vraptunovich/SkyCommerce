package com.rvk.skycommerce.mapper;


import com.rvk.skycommerce.model.IndividualClientModel;
import com.rvk.skycommerce.model.ProfessionalClientModel;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.ProfessionalClient;


public class ClientMapper {

    public static IndividualClientModel toModel(IndividualClient entity) {
        return IndividualClientModel.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .build();
    }

    public static ProfessionalClientModel toModel(ProfessionalClient entity) {
        return ProfessionalClientModel.builder()
                .id(entity.getId())
                .companyName(entity.getCompanyName())
                .registrationNumber(entity.getRegistrationNumber())
                .annualRevenue(entity.getAnnualRevenue())
                .vatNumber(entity.getVatNumber())
                .build();
    }
}