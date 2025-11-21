package com.rvk.skycommerce.mock.service;

import com.rvk.skycommerce.exception.NotFoundException;
import com.rvk.skycommerce.model.IndividualClientModel;
import com.rvk.skycommerce.model.ProfessionalClientModel;
import com.rvk.skycommerce.repository.ClientRepository;
import com.rvk.skycommerce.repository.entity.Client;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.ProfessionalClient;
import com.rvk.skycommerce.service.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    @BeforeEach
    void setUp() {
        lenient().when(clientRepository.save(any(Client.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createIndividualClient_shouldPersistAndReturnModel() {
        IndividualClientModel result = clientService.createIndividualClient("John", "Doe");

        ArgumentCaptor<IndividualClient> captor = ArgumentCaptor.forClass(IndividualClient.class);
        verify(clientRepository).save(captor.capture());

        IndividualClient savedEntity = captor.getValue();
        assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getFirstName()).isEqualTo("John");
        assertThat(savedEntity.getLastName()).isEqualTo("Doe");

        assertThat(result.getId()).isEqualTo(savedEntity.getId());
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
    }

    @Test
    void createProfessionalClient_shouldPersistAndReturnModel() {
        ProfessionalClientModel result = clientService.createProfessionalClient(
                "Acme Corp",
                "REG-001",
                new BigDecimal("8000000.00"),
                "EU123"
        );

        ArgumentCaptor<ProfessionalClient> captor = ArgumentCaptor.forClass(ProfessionalClient.class);
        verify(clientRepository).save(captor.capture());

        ProfessionalClient savedEntity = captor.getValue();
        assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getCompanyName()).isEqualTo("Acme Corp");
        assertThat(savedEntity.getRegistrationNumber()).isEqualTo("REG-001");
        assertThat(savedEntity.getAnnualRevenue()).isEqualByComparingTo("8000000.00");
        assertThat(savedEntity.getVatNumber()).isEqualTo("EU123");

        assertThat(result.getId()).isEqualTo(savedEntity.getId());
        assertThat(result.getCompanyName()).isEqualTo("Acme Corp");
        assertThat(result.getRegistrationNumber()).isEqualTo("REG-001");
        assertThat(result.getAnnualRevenue()).isEqualByComparingTo("8000000.00");
        assertThat(result.getVatNumber()).isEqualTo("EU123");
    }

    @Test
    void updateIndividualClient_shouldUpdateAndReturnModel_whenClientExistsAndIsIndividual() {
        IndividualClient existing = new IndividualClient("ID123", "Old", "Name");
        when(clientRepository.findById("ID123")).thenReturn(Optional.of(existing));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IndividualClientModel result = clientService.updateIndividualClient("ID123", "New", "User");

        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getLastName()).isEqualTo("User");

        assertThat(result.getId()).isEqualTo("ID123");
        assertThat(result.getFirstName()).isEqualTo("New");
        assertThat(result.getLastName()).isEqualTo("User");
    }

    @Test
    void updateIndividualClient_shouldThrow_whenClientNotFound() {
        when(clientRepository.findById("MISSING")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> clientService.updateIndividualClient("MISSING", "John", "Doe")
        );

        assertThat(ex.getMessage()).isEqualTo("Client with id MISSING not found");
    }

    @Test
    void updateIndividualClient_shouldThrow_whenClientIsNotIndividual() {
        ProfessionalClient professional = new ProfessionalClient(
                "ID_PRO",
                "Company",
                "REG",
                new BigDecimal("1000000.00"),
                "EU"
        );
        when(clientRepository.findById("ID_PRO")).thenReturn(Optional.of(professional));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.updateIndividualClient("ID_PRO", "John", "Doe")
        );

        assertThat(ex.getMessage()).isEqualTo("Client with id ID_PRO is not a IndividualClient");
    }

    @Test
    void updateProfessionalClient_shouldUpdateAndReturnModel_whenClientExistsAndIsProfessional() {
        ProfessionalClient existing = new ProfessionalClient(
                "PRO123",
                "OldCo",
                "OLD-REG",
                new BigDecimal("5000000.00"),
                "OLD-VAT"
        );
        when(clientRepository.findById("PRO123")).thenReturn(Optional.of(existing));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfessionalClientModel result = clientService.updateProfessionalClient(
                "PRO123",
                "NewCo",
                "NEW-REG",
                new BigDecimal("7000000.00"),
                "NEW-VAT"
        );

        assertThat(existing.getCompanyName()).isEqualTo("NewCo");
        assertThat(existing.getRegistrationNumber()).isEqualTo("NEW-REG");
        assertThat(existing.getAnnualRevenue()).isEqualByComparingTo("7000000.00");
        assertThat(existing.getVatNumber()).isEqualTo("NEW-VAT");

        assertThat(result.getId()).isEqualTo("PRO123");
        assertThat(result.getCompanyName()).isEqualTo("NewCo");
        assertThat(result.getRegistrationNumber()).isEqualTo("NEW-REG");
        assertThat(result.getAnnualRevenue()).isEqualByComparingTo("7000000.00");
        assertThat(result.getVatNumber()).isEqualTo("NEW-VAT");
    }

    @Test
    void updateProfessionalClient_shouldThrow_whenClientNotFound() {
        when(clientRepository.findById("MISSING")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> clientService.updateProfessionalClient(
                        "MISSING",
                        "Co",
                        "REG",
                        new BigDecimal("1000000.00"),
                        "VAT"
                )
        );

        assertThat(ex.getMessage()).isEqualTo("Client with id MISSING not found");
    }

    @Test
    void updateProfessionalClient_shouldThrow_whenClientIsNotProfessional() {
        IndividualClient individual = new IndividualClient("IND1", "John", "Doe");
        when(clientRepository.findById("IND1")).thenReturn(Optional.of(individual));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.updateProfessionalClient(
                        "IND1",
                        "Co",
                        "REG",
                        new BigDecimal("1000000.00"),
                        "VAT"
                )
        );

        assertThat(ex.getMessage()).isEqualTo("Client with id IND1 is not a ProfessionalClient");
    }

    @Test
    void getIndividualById_shouldReturnModel_whenIndividualExists() {
        IndividualClient entity = new IndividualClient("ID123", "Jane", "Doe");
        when(clientRepository.findById("ID123")).thenReturn(Optional.of(entity));

        IndividualClientModel result = clientService.getIndividualById("ID123");

        assertThat(result.getId()).isEqualTo("ID123");
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Doe");
    }

    @Test
    void getIndividualById_shouldThrow_whenClientNotFound() {
        when(clientRepository.findById("MISSING")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> clientService.getIndividualById("MISSING")
        );

        assertThat(ex.getMessage()).isEqualTo("Client with id MISSING not found");
    }

    @Test
    void getIndividualById_shouldThrow_whenClientIsNotIndividual() {
        ProfessionalClient professional = new ProfessionalClient(
                "PRO1",
                "Co",
                "REG",
                new BigDecimal("1.00"),
                "VAT"
        );
        when(clientRepository.findById("PRO1")).thenReturn(Optional.of(professional));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.getIndividualById("PRO1")
        );

        assertThat(ex.getMessage()).isEqualTo("Client with id PRO1 is not a IndividualClient");
    }

    @Test
    void getProfessionalById_shouldReturnModel_whenProfessionalExists() {
        ProfessionalClient entity = new ProfessionalClient(
                "PRO1",
                "Co",
                "REG",
                new BigDecimal("5000000.00"),
                "VAT"
        );
        when(clientRepository.findById("PRO1")).thenReturn(Optional.of(entity));

        ProfessionalClientModel result = clientService.getProfessionalById("PRO1");

        assertThat(result.getId()).isEqualTo("PRO1");
        assertThat(result.getCompanyName()).isEqualTo("Co");
        assertThat(result.getRegistrationNumber()).isEqualTo("REG");
        assertThat(result.getAnnualRevenue()).isEqualByComparingTo("5000000.00");
        assertThat(result.getVatNumber()).isEqualTo("VAT");
    }

    @Test
    void getProfessionalById_shouldThrow_whenClientNotFound() {
        when(clientRepository.findById("MISSING")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> clientService.getProfessionalById("MISSING")
        );

        assertThat(ex.getMessage()).isEqualTo("Client with id MISSING not found");
    }

    @Test
    void getProfessionalById_shouldThrow_whenClientIsNotProfessional() {
        IndividualClient individual = new IndividualClient("IND1", "John", "Doe");
        when(clientRepository.findById("IND1")).thenReturn(Optional.of(individual));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.getProfessionalById("IND1")
        );

        assertThat(ex.getMessage()).isEqualTo("Client with id IND1 is not a ProfessionalClient");
    }

    @Test
    void getAllIndividuals_shouldReturnOnlyIndividualsMappedToModels() {
        IndividualClient ind1 = new IndividualClient("I1", "John", "Doe");
        IndividualClient ind2 = new IndividualClient("I2", "Anna", "Smith");

        when(clientRepository.findIndividuals(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ind1, ind2)));

        List<IndividualClientModel> result = clientService.getIndividuals(Pageable.unpaged()).getContent();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(IndividualClientModel::getId)
                .containsExactlyInAnyOrder("I1", "I2");
    }

    @Test
    void getAllProfessionals_shouldReturnOnlyProfessionalsMappedToModels() {
        ProfessionalClient pro1 = new ProfessionalClient(
                "P1",
                "Co1",
                "REG1",
                new BigDecimal("1000000.00"),
                "VAT1"
        );
        ProfessionalClient pro2 = new ProfessionalClient(
                "P2",
                "Co2",
                "REG2",
                new BigDecimal("2000000.00"),
                "VAT2"
        );

        when(clientRepository.findProfessionals(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(pro1, pro2)));

        List<ProfessionalClientModel> result = clientService.getProfessionals(Pageable.unpaged()).getContent();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(ProfessionalClientModel::getId)
                .containsExactlyInAnyOrder("P1", "P2");
    }
}
