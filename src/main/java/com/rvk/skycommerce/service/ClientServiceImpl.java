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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static com.rvk.skycommerce.mapper.ClientMapper.toModel;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public IndividualClientModel createIndividualClient(String firstName, String lastName) {
        log.info("Creating individual client firstName={} lastName={}", firstName, lastName);

        String id = UUID.randomUUID().toString();
        IndividualClient entity = new IndividualClient(id, firstName, lastName);
        IndividualClient saved = clientRepository.save(entity);

        log.debug("Created individual client id={} firstName={} lastName={}", saved.getId(), firstName, lastName);

        return toModel(saved);
    }

    @Override
    @Transactional
    public ProfessionalClientModel createProfessionalClient(String companyName,
                                                            String registrationNumber,
                                                            BigDecimal annualRevenue,
                                                            String vatNumber) {
        log.info("Creating professional client companyName={} registrationNumber={}", companyName, registrationNumber);

        String id = UUID.randomUUID().toString();
        ProfessionalClient entity = new ProfessionalClient(
                id,
                companyName,
                registrationNumber,
                annualRevenue,
                vatNumber
        );
        ProfessionalClient saved = clientRepository.save(entity);

        log.debug("Created professional client id={} companyName={}", saved.getId(), companyName);

        return toModel(saved);
    }

    @Override
    public IndividualClientModel updateIndividualClient(String id, String firstName, String lastName) {
        log.info("Updating individual client id={} firstName={} lastName={}", id, firstName, lastName);

        IndividualClient individual = getIndividualClient(id);
        individual.setFirstName(firstName);
        individual.setLastName(lastName);
        IndividualClient saved = clientRepository.save(individual);

        log.debug("Updated individual client id={} firstName={} lastName={}", saved.getId(), firstName, lastName);

        return toModel(saved);
    }


    @Override
    @Transactional
    public ProfessionalClientModel updateProfessionalClient(String id,
                                                            String companyName,
                                                            String registrationNumber,
                                                            BigDecimal annualRevenue,
                                                            String vatNumber) {

        log.info("Updating professional client id={} companyName={}", id, companyName);

        ProfessionalClient professional = getProfessionalClient(id);
        professional.setCompanyName(companyName);
        professional.setRegistrationNumber(registrationNumber);
        professional.setAnnualRevenue(annualRevenue);
        professional.setVatNumber(vatNumber);
        ProfessionalClient saved = clientRepository.save(professional);

        log.debug("Updated professional client id={} companyName={}", saved.getId(), companyName);

        return toModel(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public IndividualClientModel getIndividualById(String id) {
        log.debug("Fetching individual client id={}", id);

        IndividualClient individual = getIndividualClient(id);

        log.debug("Fetched individual client id={}", id);

        return toModel(individual);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfessionalClientModel getProfessionalById(String id) {

        log.debug("Fetching professional client id={}", id);

        ProfessionalClient professional = getProfessionalClient(id);
        log.debug("Fetched professional client id={}", id);
        return toModel(professional);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IndividualClientModel> getIndividuals(Pageable pageable) {
        log.debug("Fetching individual clients: page={}", pageable);
        Page<IndividualClientModel> page = clientRepository.findIndividuals(pageable)
                .map(ClientMapper::toModel);
        log.debug("Fetched individual clients: pageNumber={} size={} returned={}", page.getNumber(), page.getSize(), page.getNumberOfElements());
        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfessionalClientModel> getProfessionals(Pageable pageable) {
        log.debug("Fetching professional clients: page={}", pageable);

        Page<ProfessionalClientModel> page = clientRepository.findProfessionals(pageable)
                .map(ClientMapper::toModel);

        log.debug("Fetched professional clients: pageNumber={} size={} returned={}", page.getNumber(), page.getSize(), page.getNumberOfElements());
        return page;
    }

    private <T extends Client> T getClient(String id, Class<T> type) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Client with id {} not found", id);
                    return new NotFoundException("Client with id " + id + " not found");
                });
        if (!type.isInstance(client)) {
            log.warn("Client with id {} is not a {}", id, type.getSimpleName());
            throw new IllegalArgumentException("Client with id " + id + " is not a " + type.getSimpleName());
        }
        return type.cast(client);
    }

    private IndividualClient getIndividualClient(String id) {
        return getClient(id, IndividualClient.class);
    }

    private ProfessionalClient getProfessionalClient(String id) {
        return getClient(id, ProfessionalClient.class);
    }

}
