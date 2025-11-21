package com.rvk.skycommerce.service;

import com.rvk.skycommerce.model.IndividualClientModel;
import com.rvk.skycommerce.model.ProfessionalClientModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ClientService {

    IndividualClientModel createIndividualClient(String firstName, String lastName);

    ProfessionalClientModel createProfessionalClient(String companyName,
                                                     String registrationNumber,
                                                     BigDecimal annualRevenue,
                                                     String vatNumber);

    IndividualClientModel updateIndividualClient(String id, String firstName, String lastName);

    ProfessionalClientModel updateProfessionalClient(String id,
                                                     String companyName,
                                                     String registrationNumber,
                                                     BigDecimal annualRevenue,
                                                     String vatNumber);

    IndividualClientModel getIndividualById(String id);

    ProfessionalClientModel getProfessionalById(String id);

    Page<IndividualClientModel> getIndividuals(Pageable pageable);

    Page<ProfessionalClientModel> getProfessionals(Pageable pageable);
}
