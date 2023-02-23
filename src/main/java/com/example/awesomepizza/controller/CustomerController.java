package com.example.awesomepizza.controller;

import com.example.awesomepizza.domain.Customer;
import com.example.awesomepizza.service.CustomerService;
import com.example.awesomepizza.dto.CustomerDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@PreAuthorize("isAuthenticated()")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final ModelMapper mapper;

    private CustomerDto convertToDto(Customer customer) {
        return mapper.map(customer, CustomerDto.class);
    }

    private Customer convertToEntity(@Valid CustomerDto customerDto) {
        return mapper.map(customerDto, Customer.class);
    }

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getCustomers() {
        List<Customer> customers = customerService.getCustomers();
        List<CustomerDto> customerDtos = customers.stream().map(this::convertToDto).collect(Collectors.toList());
        return new ResponseEntity<>(customerDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable("id") long id) {
        CustomerDto customerDto = convertToDto(customerService.getCustomer(id));
        return new ResponseEntity<>(customerDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody @Valid CustomerDto customerDto) {
        Customer customer = customerService.createCustomer(convertToEntity(customerDto));
        return new ResponseEntity<>(convertToDto(customer), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateCustomer(
            @PathVariable("id") Long id,
            @Valid @RequestBody CustomerDto customerDto
    ) {
        if (!id.equals(customerDto.getId())) throw new
                ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "id does not match."
        );

        customerService.updateCustomer(id, convertToEntity(customerDto));
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteCustomer(@PathVariable("id") Long id) {
        customerService.deleteCustomer(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
