package com.rvk.skycommerce.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("INDIVIDUAL")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IndividualClient extends Client {

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    public IndividualClient(String id, String firstName, String lastName) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public IndividualClient(String id) {
        super(id);
    }
}
