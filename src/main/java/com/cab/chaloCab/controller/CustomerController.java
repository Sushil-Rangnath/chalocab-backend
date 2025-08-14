package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.CustomerRequest;
import com.cab.chaloCab.dto.CustomerResponse;
import com.cab.chaloCab.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(customerService.getAllCustomers(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(
            @PathVariable Long id,
            @RequestBody CustomerRequest request
    ) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    // ✅ Soft delete (recommended to keep data but mark inactive)
    @DeleteMapping("/{id}/soft")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        customerService.softDeleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Hard delete (permanent removal)
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer permanently deleted successfully");
    }

    // ✅ Search by phone or name/email (can be expanded)
    @GetMapping("/search")
    public ResponseEntity<Page<CustomerResponse>> searchCustomers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(customerService.searchCustomers(keyword, pageable));
    }

}
