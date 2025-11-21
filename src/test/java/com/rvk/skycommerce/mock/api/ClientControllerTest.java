package com.rvk.skycommerce.mock.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rvk.skycommerce.api.ClientController;
import com.rvk.skycommerce.api.dto.CreateIndividualClientRequest;
import com.rvk.skycommerce.api.dto.CreateProfessionalClientRequest;
import com.rvk.skycommerce.api.dto.UpdateIndividualClientRequest;
import com.rvk.skycommerce.api.dto.UpdateProfessionalClientRequest;
import com.rvk.skycommerce.model.IndividualClientModel;
import com.rvk.skycommerce.model.ProfessionalClientModel;
import com.rvk.skycommerce.service.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    @Test
    void createIndividual_shouldReturnCreatedIndividual() throws Exception {
        CreateIndividualClientRequest request = new CreateIndividualClientRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        IndividualClientModel model = IndividualClientModel.builder()
                .id("IND-1")
                .firstName("John")
                .lastName("Doe")
                .build();

        given(clientService.createIndividualClient("John", "Doe"))
                .willReturn(model);

        mockMvc.perform(post("/api/clients/individual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("IND-1")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")));
    }

    @Test
    void createProfessional_shouldReturnCreatedProfessional() throws Exception {
        CreateProfessionalClientRequest request = new CreateProfessionalClientRequest();
        request.setCompanyName("Acme");
        request.setRegistrationNumber("REG-1");
        request.setAnnualRevenue(new BigDecimal("8000000.00"));
        request.setVatNumber("EU123");

        ProfessionalClientModel model = ProfessionalClientModel.builder()
                .id("PRO-1")
                .companyName("Acme")
                .registrationNumber("REG-1")
                .annualRevenue(new BigDecimal("8000000.00"))
                .vatNumber("EU123")
                .build();

        given(clientService.createProfessionalClient(
                "Acme",
                "REG-1",
                new BigDecimal("8000000.00"),
                "EU123"
        )).willReturn(model);

        mockMvc.perform(post("/api/clients/professional")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("PRO-1")))
                .andExpect(jsonPath("$.companyName", is("Acme")))
                .andExpect(jsonPath("$.registrationNumber", is("REG-1")))
                .andExpect(jsonPath("$.annualRevenue", is(8000000.00)))
                .andExpect(jsonPath("$.vatNumber", is("EU123")));
    }

    @Test
    void updateIndividual_shouldReturnUpdatedIndividual() throws Exception {
        UpdateIndividualClientRequest request = new UpdateIndividualClientRequest();
        request.setFirstName("New");
        request.setLastName("Name");

        IndividualClientModel model = IndividualClientModel.builder()
                .id("IND-1")
                .firstName("New")
                .lastName("Name")
                .build();

        given(clientService.updateIndividualClient("IND-1", "New", "Name"))
                .willReturn(model);

        mockMvc.perform(put("/api/clients/individual/{id}", "IND-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("IND-1")))
                .andExpect(jsonPath("$.firstName", is("New")))
                .andExpect(jsonPath("$.lastName", is("Name")));
    }

    @Test
    void updateProfessional_shouldReturnUpdatedProfessional() throws Exception {
        UpdateProfessionalClientRequest request = new UpdateProfessionalClientRequest();
        request.setCompanyName("NewCo");
        request.setRegistrationNumber("REG-2");
        request.setAnnualRevenue(new BigDecimal("9000000.00"));
        request.setVatNumber("EU999");

        ProfessionalClientModel model = ProfessionalClientModel.builder()
                .id("PRO-1")
                .companyName("NewCo")
                .registrationNumber("REG-2")
                .annualRevenue(new BigDecimal("9000000.00"))
                .vatNumber("EU999")
                .build();

        given(clientService.updateProfessionalClient(
                "PRO-1",
                "NewCo",
                "REG-2",
                new BigDecimal("9000000.00"),
                "EU999"
        )).willReturn(model);

        mockMvc.perform(put("/api/clients/professional/{id}", "PRO-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("PRO-1")))
                .andExpect(jsonPath("$.companyName", is("NewCo")))
                .andExpect(jsonPath("$.registrationNumber", is("REG-2")))
                .andExpect(jsonPath("$.annualRevenue", is(9000000.00)))
                .andExpect(jsonPath("$.vatNumber", is("EU999")));
    }

    @Test
    void getIndividual_shouldReturnIndividual() throws Exception {
        IndividualClientModel model = IndividualClientModel.builder()
                .id("IND-1")
                .firstName("John")
                .lastName("Doe")
                .build();

        given(clientService.getIndividualById("IND-1"))
                .willReturn(model);

        mockMvc.perform(get("/api/clients/individual/{id}", "IND-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("IND-1")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")));
    }

    @Test
    void getProfessional_shouldReturnProfessional() throws Exception {
        ProfessionalClientModel model = ProfessionalClientModel.builder()
                .id("PRO-1")
                .companyName("Acme")
                .registrationNumber("REG-1")
                .annualRevenue(new BigDecimal("8000000.00"))
                .vatNumber("EU123")
                .build();

        given(clientService.getProfessionalById("PRO-1"))
                .willReturn(model);

        mockMvc.perform(get("/api/clients/professional/{id}", "PRO-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("PRO-1")))
                .andExpect(jsonPath("$.companyName", is("Acme")))
                .andExpect(jsonPath("$.registrationNumber", is("REG-1")))
                .andExpect(jsonPath("$.annualRevenue", is(8000000.00)))
                .andExpect(jsonPath("$.vatNumber", is("EU123")));
    }

    @Test
    void getIndividuals_shouldReturnPagedIndividuals() throws Exception {
        IndividualClientModel m1 = IndividualClientModel.builder()
                .id("I1")
                .firstName("A")
                .lastName("One")
                .build();
        IndividualClientModel m2 = IndividualClientModel.builder()
                .id("I2")
                .firstName("B")
                .lastName("Two")
                .build();

        Page<IndividualClientModel> page = new PageImpl<>(List.of(m1, m2));

        given(clientService.getIndividuals(any()))
                .willReturn(page);

        mockMvc.perform(get("/api/clients/individual")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is("I1")))
                .andExpect(jsonPath("$.content[1].id", is("I2")));
    }

    @Test
    void getProfessionals_shouldReturnPagedProfessionals() throws Exception {
        ProfessionalClientModel p1 = ProfessionalClientModel.builder()
                .id("P1")
                .companyName("Co1")
                .registrationNumber("REG1")
                .annualRevenue(new BigDecimal("1000000.00"))
                .vatNumber("VAT1")
                .build();
        ProfessionalClientModel p2 = ProfessionalClientModel.builder()
                .id("P2")
                .companyName("Co2")
                .registrationNumber("REG2")
                .annualRevenue(new BigDecimal("2000000.00"))
                .vatNumber("VAT2")
                .build();

        Page<ProfessionalClientModel> page = new PageImpl<>(List.of(p1, p2));

        given(clientService.getProfessionals(any()))
                .willReturn(page);

        mockMvc.perform(get("/api/clients/professional")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is("P1")))
                .andExpect(jsonPath("$.content[1].id", is("P2")));
    }
}
