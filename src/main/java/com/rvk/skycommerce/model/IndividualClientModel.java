package com.rvk.skycommerce.model;


import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class IndividualClientModel {
    String id;
    String firstName;
    String lastName;
}