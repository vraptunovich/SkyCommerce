package com.rvk.skycommerce.api.dto.client;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class IndividualClientResponse {
    String id;
    String firstName;
    String lastName;
}
