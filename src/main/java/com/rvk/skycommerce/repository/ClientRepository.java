package com.rvk.skycommerce.repository;

import com.rvk.skycommerce.repository.entity.Client;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.ProfessionalClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {


    @Query("select i from IndividualClient i")
    Page<IndividualClient> findIndividuals(Pageable pageable);

    @Query("select p from ProfessionalClient p")
    Page<ProfessionalClient> findProfessionals(Pageable pageable);
}
