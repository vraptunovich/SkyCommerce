package com.rvk.skycommerce.mock;

import com.rvk.skycommerce.model.ShoppingCartModel;
import com.rvk.skycommerce.service.ClientService;
import com.rvk.skycommerce.service.ShoppingCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfigTest.TestBeans.class)
@TestPropertySource(properties = {
        "app.security.admin.username=admin",
        "app.security.admin.password=temp123",
        "app.security.user.password=user123",
        "app.security.user.password=temp123"
})
class SecurityConfigTest {

    @TestConfiguration
    static class TestBeans {

        @Bean
        ClientService clientService() {
            return Mockito.mock(ClientService.class);
        }

        @Bean
        ShoppingCartService shoppingCartService() {
            return Mockito.mock(ShoppingCartService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @BeforeEach
    void setUp() {
        Mockito.when(clientService.getIndividuals(any(Pageable.class)))
                .thenReturn(Page.empty());

        Mockito.when(shoppingCartService.getCart(eq(1L)))
                .thenReturn(ShoppingCartModel.builder()
                        .id(1L)
                        .clientId("clientId")
                        .items(Collections.emptyList())
                        .totalAmount(BigDecimal.ZERO)
                        .build());
    }

    @Test
    void anonymousCannotAccessClients() throws Exception {
        mockMvc.perform(get("/api/clients/individual"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userCannotAccessClients() throws Exception {
        mockMvc.perform(get("/api/clients/individual")
                        .with(httpBasic("user", "temp123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessClients() throws Exception {
        mockMvc.perform(get("/api/clients/individual")
                        .with(httpBasic("admin", "temp123")))
                .andExpect(status().isOk());
    }

    @Test
    void anonymousCannotAccessCarts() throws Exception {
        mockMvc.perform(get("/api/carts/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userCanAccessCarts() throws Exception {
        mockMvc.perform(get("/api/carts/1")
                        .with(httpBasic("user", "temp123")))
                .andExpect(status().isOk());
    }

    @Test
    void adminCanAccessCarts() throws Exception {
        mockMvc.perform(get("/api/carts/1")
                        .with(httpBasic("admin", "temp123")))
                .andExpect(status().isOk());
    }
}
