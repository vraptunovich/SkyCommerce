package com.rvk.skycommerce.service;

import com.rvk.skycommerce.exception.NotFoundException;
import com.rvk.skycommerce.mapper.ClientMapper;
import com.rvk.skycommerce.model.IndividualClientModel;
import com.rvk.skycommerce.model.ProfessionalClientModel;
import com.rvk.skycommerce.repository.ClientRepository;
import com.rvk.skycommerce.repository.entity.Client;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.ProfessionalClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static com.rvk.skycommerce.mapper.ClientMapper.toModel;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public IndividualClientModel createIndividualClient(String firstName, String lastName) {
        String id = UUID.randomUUID().toString();
        IndividualClient entity = new IndividualClient(id, firstName, lastName);
        IndividualClient saved = clientRepository.save(entity);
        return toModel(saved);
    }

    @Override
    @Transactional
    public ProfessionalClientModel createProfessionalClient(String companyName,
                                                            String registrationNumber,
                                                            BigDecimal annualRevenue,
                                                            String vatNumber) {
        String id = UUID.randomUUID().toString();
        ProfessionalClient entity = new ProfessionalClient(
                id,
                companyName,
                registrationNumber,
                annualRevenue,
                vatNumber
        );
        ProfessionalClient saved = clientRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public IndividualClientModel updateIndividualClient(String id, String firstName, String lastName) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client with id " + id + " not found"));
        if (!(client instanceof IndividualClient individual)) {
            throw new IllegalArgumentException("Client with id " + id + " is not an individual client");
        }
        individual.setFirstName(firstName);
        individual.setLastName(lastName);
        IndividualClient saved = clientRepository.save(individual);
        return toModel(saved);
    }

    @Override
    @Transactional
    public ProfessionalClientModel updateProfessionalClient(String id,
                                                            String companyName,
                                                            String registrationNumber,
                                                            BigDecimal annualRevenue,
                                                            String vatNumber) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client with id " + id + " not found"));
        if (!(client instanceof ProfessionalClient professional)) {
            throw new IllegalArgumentException("Client with id " + id + " is not a professional client");
        }
        professional.setCompanyName(companyName);
        professional.setRegistrationNumber(registrationNumber);
        professional.setAnnualRevenue(annualRevenue);
        professional.setVatNumber(vatNumber);
        ProfessionalClient saved = clientRepository.save(professional);
        return toModel(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public IndividualClientModel getIndividualById(String id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client with id " + id + " not found"));
        if (!(client instanceof IndividualClient individual)) {
            throw new IllegalArgumentException("Client with id " + id + " is not an individual client");
        }
        return toModel(individual);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfessionalClientModel getProfessionalById(String id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client with id " + id + " not found"));
        if (!(client instanceof ProfessionalClient professional)) {
            throw new IllegalArgumentException("Client with id " + id + " is not a professional client");
        }
        return toModel(professional);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IndividualClientModel> getIndividuals(Pageable pageable) {
        return clientRepository.findIndividuals(pageable)
                .map(ClientMapper::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfessionalClientModel> getProfessionals(Pageable pageable) {
        return clientRepository.findProfessionals(pageable)
                .map(ClientMapper::toModel);
    }
}
