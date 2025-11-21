package com.rvk.skycommerce.integration.service;

import com.rvk.skycommerce.model.IndividualClientModel;
import com.rvk.skycommerce.model.ProfessionalClientModel;
import com.rvk.skycommerce.repository.ClientRepository;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.ProfessionalClient;
import com.rvk.skycommerce.service.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void createIndividualClient_persistsEntityAndReturnsModel() {
        IndividualClientModel model = clientService.createIndividualClient("John", "Doe");

        assertThat(model.getId()).isNotNull();
        assertThat(model.getFirstName()).isEqualTo("John");
        assertThat(model.getLastName()).isEqualTo("Doe");

        IndividualClient entity = (IndividualClient) clientRepository.findById(model.getId()).orElseThrow();
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isEqualTo("Doe");
    }

    @Test
    void createProfessionalClient_persistsEntityAndReturnsModel() {
        ProfessionalClientModel model = clientService.createProfessionalClient(
                "Acme Corp",
                "REG-001",
                new BigDecimal("8000000.00"),
                "EU123"
        );

        assertThat(model.getId()).isNotNull();
        assertThat(model.getCompanyName()).isEqualTo("Acme Corp");
        assertThat(model.getRegistrationNumber()).isEqualTo("REG-001");
        assertThat(model.getAnnualRevenue()).isEqualByComparingTo("8000000.00");
        assertThat(model.getVatNumber()).isEqualTo("EU123");

        ProfessionalClient entity = (ProfessionalClient) clientRepository.findById(model.getId()).orElseThrow();
        assertThat(entity.getCompanyName()).isEqualTo("Acme Corp");
        assertThat(entity.getRegistrationNumber()).isEqualTo("REG-001");
        assertThat(entity.getAnnualRevenue()).isEqualByComparingTo("8000000.00");
        assertThat(entity.getVatNumber()).isEqualTo("EU123");
    }

    @Test
    void updateIndividualClient_updatesEntityAndReturnsModel() {
        IndividualClientModel created = clientService.createIndividualClient("Old", "Name");

        IndividualClientModel updated = clientService.updateIndividualClient(created.getId(), "New", "User");

        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getFirstName()).isEqualTo("New");
        assertThat(updated.getLastName()).isEqualTo("User");

        IndividualClient entity = (IndividualClient) clientRepository.findById(created.getId()).orElseThrow();
        assertThat(entity.getFirstName()).isEqualTo("New");
        assertThat(entity.getLastName()).isEqualTo("User");
    }

    @Test
    void updateProfessionalClient_updatesEntityAndReturnsModel() {
        ProfessionalClientModel created = clientService.createProfessionalClient(
                "OldCo",
                "OLD-REG",
                new BigDecimal("5000000.00"),
                "OLD-VAT"
        );

        ProfessionalClientModel updated = clientService.updateProfessionalClient(
                created.getId(),
                "NewCo",
                "NEW-REG",
                new BigDecimal("7000000.00"),
                "NEW-VAT"
        );

        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getCompanyName()).isEqualTo("NewCo");
        assertThat(updated.getRegistrationNumber()).isEqualTo("NEW-REG");
        assertThat(updated.getAnnualRevenue()).isEqualByComparingTo("7000000.00");
        assertThat(updated.getVatNumber()).isEqualTo("NEW-VAT");

        ProfessionalClient entity = (ProfessionalClient) clientRepository.findById(created.getId()).orElseThrow();
        assertThat(entity.getCompanyName()).isEqualTo("NewCo");
        assertThat(entity.getRegistrationNumber()).isEqualTo("NEW-REG");
        assertThat(entity.getAnnualRevenue()).isEqualByComparingTo("7000000.00");
        assertThat(entity.getVatNumber()).isEqualTo("NEW-VAT");
    }

    @Test
    void getIndividualById_returnsIndividualModel() {
        IndividualClientModel created = clientService.createIndividualClient("Jane", "Doe");

        IndividualClientModel found = clientService.getIndividualById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getFirstName()).isEqualTo("Jane");
        assertThat(found.getLastName()).isEqualTo("Doe");
    }

    @Test
    void getIndividualById_throwsWhenClientIsNotIndividual() {
        ProfessionalClient professional = new ProfessionalClient(
                "PRO1",
                "Co",
                "REG",
                new BigDecimal("1000000.00"),
                "VAT"
        );
        clientRepository.save(professional);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.getIndividualById("PRO1")
        );

        assertThat(ex.getMessage()).isEqualTo("Client with id PRO1 is not a IndividualClient");
    }

    @Test
    void getProfessionalById_returnsProfessionalModel() {
        ProfessionalClientModel created = clientService.createProfessionalClient(
                "ProCo",
                "REG-PRO",
                new BigDecimal("9000000.00"),
                "VAT-PRO"
        );

        ProfessionalClientModel found = clientService.getProfessionalById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getCompanyName()).isEqualTo("ProCo");
        assertThat(found.getRegistrationNumber()).isEqualTo("REG-PRO");
        assertThat(found.getAnnualRevenue()).isEqualByComparingTo("9000000.00");
        assertThat(found.getVatNumber()).isEqualTo("VAT-PRO");
    }

    @Test
    void getProfessionalById_throwsWhenClientIsNotProfessional() {
        IndividualClient individual = new IndividualClient("IND1", "John", "Doe");
        clientRepository.save(individual);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.getProfessionalById("IND1")
        );

        assertThat(ex.getMessage()).isEqualTo("Client with id IND1 is not a ProfessionalClient");
    }

    @Test
    void getAllIndividuals_returnsOnlyIndividuals() {
        long before = clientService.getIndividuals(Pageable.unpaged()).getTotalElements();

        IndividualClientModel ind1 = clientService.createIndividualClient("A", "One");
        IndividualClientModel ind2 = clientService.createIndividualClient("B", "Two");
        clientService.createProfessionalClient(
                "ProX",
                "REG-X",
                new BigDecimal("1000000.00"),
                "VAT-X"
        );

        List<IndividualClientModel> allIndividuals = clientService.getIndividuals(Pageable.unpaged()).getContent();

        assertThat(allIndividuals).hasSizeGreaterThanOrEqualTo((int) (before + 2));
        assertThat(allIndividuals)
                .extracting(IndividualClientModel::getId)
                .contains(ind1.getId(), ind2.getId());
    }

    @Test
    void getAllProfessionals_returnsOnlyProfessionals() {
        int before = (int) clientService.getProfessionals(Pageable.unpaged()).getTotalElements();

        clientService.createIndividualClient("A", "One");
        ProfessionalClientModel pro1 = clientService.createProfessionalClient(
                "Pro1",
                "REG1",
                new BigDecimal("2000000.00"),
                "VAT1"
        );
        ProfessionalClientModel pro2 = clientService.createProfessionalClient(
                "Pro2",
                "REG2",
                new BigDecimal("3000000.00"),
                "VAT2"
        );

        List<ProfessionalClientModel> allProfessionals = clientService.getProfessionals(Pageable.unpaged()).getContent();

        assertThat(allProfessionals).hasSizeGreaterThanOrEqualTo(before + 2);
        assertThat(allProfessionals)
                .extracting(ProfessionalClientModel::getId)
                .contains(pro1.getId(), pro2.getId());
    }
}






