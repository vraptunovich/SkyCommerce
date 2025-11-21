package com.rvk.skycommerce.api;

import com.rvk.skycommerce.api.dto.*;
import com.rvk.skycommerce.api.mapper.ClientResponseMapper;
import com.rvk.skycommerce.model.IndividualClientModel;
import com.rvk.skycommerce.model.ProfessionalClientModel;
import com.rvk.skycommerce.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.rvk.skycommerce.api.mapper.ClientResponseMapper.toResponse;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping("/individual")
    @ResponseStatus(HttpStatus.CREATED)
    public IndividualClientResponse createIndividual(@Valid @RequestBody CreateIndividualClientRequest request) {
        IndividualClientModel model = clientService.createIndividualClient(request.getFirstName(), request.getLastName());
        return toResponse(model);
    }

    @PostMapping("/professional")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfessionalClientResponse createProfessional(@Valid @RequestBody CreateProfessionalClientRequest request) {
        ProfessionalClientModel model = clientService.createProfessionalClient(
                request.getCompanyName(),
                request.getRegistrationNumber(),
                request.getAnnualRevenue(),
                request.getVatNumber()
        );
        return toResponse(model);
    }

    @PutMapping("/individual/{id}")
    public IndividualClientResponse updateIndividual(@PathVariable String id,
                                                     @Valid @RequestBody UpdateIndividualClientRequest request) {
        IndividualClientModel model = clientService.updateIndividualClient(
                id,
                request.getFirstName(),
                request.getLastName()
        );
        return toResponse(model);
    }

    @PutMapping("/professional/{id}")
    public ProfessionalClientResponse updateProfessional(@PathVariable String id,
                                                         @Valid @RequestBody UpdateProfessionalClientRequest request) {
        ProfessionalClientModel model = clientService.updateProfessionalClient(
                id,
                request.getCompanyName(),
                request.getRegistrationNumber(),
                request.getAnnualRevenue(),
                request.getVatNumber()
        );
        return toResponse(model);
    }

    @GetMapping("/individual/{id}")
    public IndividualClientResponse getIndividual(@PathVariable String id) {
        IndividualClientModel model = clientService.getIndividualById(id);
        return toResponse(model);
    }

    @GetMapping("/professional/{id}")
    public ProfessionalClientResponse getProfessional(@PathVariable String id) {
        ProfessionalClientModel model = clientService.getProfessionalById(id);
        return toResponse(model);
    }

    @GetMapping("/individual")
    public Page<IndividualClientResponse> getIndividuals(@PageableDefault(size = 20) Pageable pageable) {
        return clientService.getIndividuals(pageable)
                .map(ClientResponseMapper::toResponse);
    }

    @GetMapping("/professional")
    public Page<ProfessionalClientResponse> getProfessionals(@PageableDefault(size = 20) Pageable pageable) {
        return clientService.getProfessionals(pageable)
                .map(ClientResponseMapper::toResponse);
    }
}
