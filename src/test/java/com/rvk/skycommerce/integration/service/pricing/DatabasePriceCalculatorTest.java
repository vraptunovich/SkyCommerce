package com.rvk.skycommerce.integration.service.pricing;

import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.ProfessionalClient;
import com.rvk.skycommerce.service.pricing.DatabasePriceCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "skycommerce.pricing.mode=db")
class DatabasePriceCalculatorTest {

    @Autowired
    private DatabasePriceCalculator calculator;

    @Test
    void shouldReturnPriceForIndividualClient() {
        IndividualClient client = new IndividualClient("TEST_IND", "John", "Doe");

        BigDecimal pricePhone = calculator.getUnitPrice(client, ProductType.HIGH_END_PHONE);
        BigDecimal priceMid = calculator.getUnitPrice(client, ProductType.MID_RANGE_PHONE);
        BigDecimal priceLaptop = calculator.getUnitPrice(client, ProductType.LAPTOP);

        assertThat(pricePhone).isEqualByComparingTo("1500.00");
        assertThat(priceMid).isEqualByComparingTo("800.00");
        assertThat(priceLaptop).isEqualByComparingTo("1200.00");
    }

    @Test
    void shouldReturnPriceForProfessionalLowRevenueClient() {
        ProfessionalClient client = new ProfessionalClient(
                "TEST_PRO_LOW",
                "LowCorp",
                "REG-LOW",
                new BigDecimal("8000000.00"),
                "EU-LOW"
        );

        BigDecimal pricePhone = calculator.getUnitPrice(client, ProductType.HIGH_END_PHONE);
        BigDecimal priceMid = calculator.getUnitPrice(client, ProductType.MID_RANGE_PHONE);
        BigDecimal priceLaptop = calculator.getUnitPrice(client, ProductType.LAPTOP);

        assertThat(pricePhone).isEqualByComparingTo("1150.00");
        assertThat(priceMid).isEqualByComparingTo("600.00");
        assertThat(priceLaptop).isEqualByComparingTo("1000.00");
    }

    @Test
    void shouldReturnPriceForProfessionalHighRevenueClient() {
        ProfessionalClient client = new ProfessionalClient(
                "TEST_PRO_HIGH",
                "HighCorp",
                "REG-HIGH",
                new BigDecimal("20000000.00"),
                "EU-HIGH"
        );

        BigDecimal pricePhone = calculator.getUnitPrice(client, ProductType.HIGH_END_PHONE);
        BigDecimal priceMid = calculator.getUnitPrice(client, ProductType.MID_RANGE_PHONE);
        BigDecimal priceLaptop = calculator.getUnitPrice(client, ProductType.LAPTOP);

        assertThat(pricePhone).isEqualByComparingTo("1000.00");
        assertThat(priceMid).isEqualByComparingTo("550.00");
        assertThat(priceLaptop).isEqualByComparingTo("900.00");
    }
}
