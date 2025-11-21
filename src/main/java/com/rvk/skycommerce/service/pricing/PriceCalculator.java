package com.rvk.skycommerce.service.pricing;


import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.repository.entity.Client;

import java.math.BigDecimal;

public interface PriceCalculator {

    BigDecimal getUnitPrice(Client client, ProductType productType);
}
