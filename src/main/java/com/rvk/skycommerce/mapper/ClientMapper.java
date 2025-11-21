package com.rvk.skycommerce.mapper;

import com.rvk.skycommerce.model.IndividualClientModel;
import com.rvk.skycommerce.model.ProfessionalClientModel;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.ProfessionalClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientMapper {

    public static IndividualClientModel toModel(IndividualClient entity) {
        if (entity == null) {
            log.warn("toModel(IndividualClient) received null");
            return null;
        }
        IndividualClientModel model = IndividualClientModel.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .build();
        log.trace("Mapped IndividualClient id={} -> {}", entity.getId(), model);
        return model;
    }

    public static ProfessionalClientModel toModel(ProfessionalClient entity) {
        if (entity == null) {
            log.warn("toModel(ProfessionalClient) received null");
            return null;
        }
        ProfessionalClientModel model = ProfessionalClientModel.builder()
                .id(entity.getId())
                .companyName(entity.getCompanyName())
                .registrationNumber(entity.getRegistrationNumber())
                .annualRevenue(entity.getAnnualRevenue())
                .vatNumber(entity.getVatNumber())
                .build();
        log.trace("Mapped ProfessionalClient id={} -> {}", entity.getId(), model);
        return model;
    }
}
