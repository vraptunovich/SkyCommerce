package com.rvk.skycommerce.mock.mapper;

import com.rvk.skycommerce.mapper.ClientMapper;
import com.rvk.skycommerce.model.IndividualClientModel;
import com.rvk.skycommerce.model.ProfessionalClientModel;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.ProfessionalClient;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ClientMapperTest {

    @Test
    void toModel_shouldMapIndividualClient() {
        IndividualClient entity = new IndividualClient("ID123", "John", "Doe");

        IndividualClientModel model = ClientMapper.toModel(entity);

        assertThat(model.getId()).isEqualTo("ID123");
        assertThat(model.getFirstName()).isEqualTo("John");
        assertThat(model.getLastName()).isEqualTo("Doe");
    }

    @Test
    void toModel_shouldMapProfessionalClient() {
        ProfessionalClient entity = new ProfessionalClient(
                "PRO1",
                "Acme Corp",
                "REG-001",
                new BigDecimal("8000000.00"),
                "EU123456"
        );

        ProfessionalClientModel model = ClientMapper.toModel(entity);

        assertThat(model.getId()).isEqualTo("PRO1");
        assertThat(model.getCompanyName()).isEqualTo("Acme Corp");
        assertThat(model.getRegistrationNumber()).isEqualTo("REG-001");
        assertThat(model.getAnnualRevenue()).isEqualByComparingTo("8000000.00");
        assertThat(model.getVatNumber()).isEqualTo("EU123456");
    }
}
