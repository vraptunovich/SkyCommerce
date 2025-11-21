package com.rvk.skycommerce.mock.service.pricing;

import com.rvk.skycommerce.config.PricingProperties;
import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.ProfessionalClient;
import com.rvk.skycommerce.service.pricing.YamlPriceCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class YamlPriceCalculatorTest {

    private YamlPriceCalculator calculator;

    @BeforeEach
    void setUp() {
        PricingProperties properties = new PricingProperties();

        Map<ProductType, BigDecimal> individual = new EnumMap<>(ProductType.class);
        individual.put(ProductType.HIGH_END_PHONE, new BigDecimal("1500.00"));
        individual.put(ProductType.MID_RANGE_PHONE, new BigDecimal("800.00"));
        individual.put(ProductType.LAPTOP, new BigDecimal("1200.00"));
        properties.setIndividual(individual);

        PricingProperties.Tier lowTier = new PricingProperties.Tier();
        lowTier.setMaxRevenueInclusive(new BigDecimal("10000000.00"));
        Map<ProductType, BigDecimal> lowProducts = new EnumMap<>(ProductType.class);
        lowProducts.put(ProductType.HIGH_END_PHONE, new BigDecimal("1150.00"));
        lowProducts.put(ProductType.MID_RANGE_PHONE, new BigDecimal("600.00"));
        lowProducts.put(ProductType.LAPTOP, new BigDecimal("1000.00"));
        lowTier.setProducts(lowProducts);

        PricingProperties.Tier highTier = new PricingProperties.Tier();
        highTier.setMinRevenueExclusive(new BigDecimal("10000000.00"));
        Map<ProductType, BigDecimal> highProducts = new EnumMap<>(ProductType.class);
        highProducts.put(ProductType.HIGH_END_PHONE, new BigDecimal("1000.00"));
        highProducts.put(ProductType.MID_RANGE_PHONE, new BigDecimal("550.00"));
        highProducts.put(ProductType.LAPTOP, new BigDecimal("900.00"));
        highTier.setProducts(highProducts);

        PricingProperties.Professional professional = new PricingProperties.Professional();
        professional.setLowRevenue(lowTier);
        professional.setHighRevenue(highTier);

        properties.setProfessional(professional);

        calculator = new YamlPriceCalculator(properties);
    }

    @Test
    void shouldReturnPricesForIndividualClient() {
        IndividualClient client = new IndividualClient("IND_1", "John", "Doe");

        BigDecimal highEnd = calculator.getUnitPrice(client, ProductType.HIGH_END_PHONE);
        BigDecimal midRange = calculator.getUnitPrice(client, ProductType.MID_RANGE_PHONE);
        BigDecimal laptop = calculator.getUnitPrice(client, ProductType.LAPTOP);

        assertThat(highEnd).isEqualByComparingTo("1500.00");
        assertThat(midRange).isEqualByComparingTo("800.00");
        assertThat(laptop).isEqualByComparingTo("1200.00");
    }

    @Test
    void shouldReturnLowRevenuePricesForProfessionalClientBelowTenMillion() {
        ProfessionalClient client = new ProfessionalClient(
                "PRO_LOW",
                "LowCorp",
                "REG-LOW",
                new BigDecimal("8000000.00"),
                "EU-LOW"
        );

        BigDecimal highEnd = calculator.getUnitPrice(client, ProductType.HIGH_END_PHONE);
        BigDecimal midRange = calculator.getUnitPrice(client, ProductType.MID_RANGE_PHONE);
        BigDecimal laptop = calculator.getUnitPrice(client, ProductType.LAPTOP);

        assertThat(highEnd).isEqualByComparingTo("1150.00");
        assertThat(midRange).isEqualByComparingTo("600.00");
        assertThat(laptop).isEqualByComparingTo("1000.00");
    }

    @Test
    void shouldUseLowRevenueTierForExactlyTenMillion() {
        ProfessionalClient client = new ProfessionalClient(
                "PRO_EDGE",
                "EdgeCorp",
                "REG-EDGE",
                new BigDecimal("10000000.00"),
                "EU-EDGE"
        );

        BigDecimal laptop = calculator.getUnitPrice(client, ProductType.LAPTOP);

        assertThat(laptop).isEqualByComparingTo("1000.00");
    }

    @Test
    void shouldReturnHighRevenuePricesForProfessionalClientAboveTenMillion() {
        ProfessionalClient client = new ProfessionalClient(
                "PRO_HIGH",
                "HighCorp",
                "REG-HIGH",
                new BigDecimal("20000000.00"),
                "EU-HIGH"
        );

        BigDecimal highEnd = calculator.getUnitPrice(client, ProductType.HIGH_END_PHONE);
        BigDecimal midRange = calculator.getUnitPrice(client, ProductType.MID_RANGE_PHONE);
        BigDecimal laptop = calculator.getUnitPrice(client, ProductType.LAPTOP);

        assertThat(highEnd).isEqualByComparingTo("1000.00");
        assertThat(midRange).isEqualByComparingTo("550.00");
        assertThat(laptop).isEqualByComparingTo("900.00");
    }
}
