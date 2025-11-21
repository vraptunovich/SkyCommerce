package com.rvk.skycommerce.integration.repository;

import com.rvk.skycommerce.repository.ClientRepository;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.ProfessionalClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void shouldLoadTestClientsFromFlyway() {
        List<?> clients = clientRepository.findAll();
        assertThat(clients).hasSizeGreaterThanOrEqualTo(4);
        assertThat(clientRepository.findById("C_IND_001")).isPresent();
        assertThat(clientRepository.findById("C_IND_002")).isPresent();
        assertThat(clientRepository.findById("C_PRO_LOW_001")).isPresent();
        assertThat(clientRepository.findById("C_PRO_HIGH_001")).isPresent();
    }

    @Test
    void shouldSaveAndLoadIndividualClient() {
        IndividualClient client = new IndividualClient("TEST_IND_1", "Test", "User");
        clientRepository.save(client);

        Optional<?> loaded = clientRepository.findById("TEST_IND_1");
        assertThat(loaded).isPresent();
        assertThat(loaded.get()).isInstanceOf(IndividualClient.class);
        IndividualClient ic = (IndividualClient) loaded.get();
        assertThat(ic.getFirstName()).isEqualTo("Test");
        assertThat(ic.getLastName()).isEqualTo("User");
    }

    @Test
    void shouldSaveAndLoadProfessionalClient() {
        ProfessionalClient client = new ProfessionalClient(
                "TEST_PRO_1",
                "Test Company",
                "REG-001",
                new BigDecimal("12000000.00"),
                "EU000000001"
        );
        clientRepository.save(client);

        Optional<?> loaded = clientRepository.findById("TEST_PRO_1");
        assertThat(loaded).isPresent();
        assertThat(loaded.get()).isInstanceOf(ProfessionalClient.class);
        ProfessionalClient pc = (ProfessionalClient) loaded.get();
        assertThat(pc.getCompanyName()).isEqualTo("Test Company");
        assertThat(pc.getAnnualRevenue()).isEqualByComparingTo("12000000.00");
    }
}
