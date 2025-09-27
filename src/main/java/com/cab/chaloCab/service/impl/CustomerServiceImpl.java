package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.CustomerRequest;
import com.cab.chaloCab.dto.CustomerResponse;
import com.cab.chaloCab.entity.Customer;
import com.cab.chaloCab.enums.Role;
import com.cab.chaloCab.repository.CustomerRepository;
import com.cab.chaloCab.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists.");
        }
        if (customerRepository.existsByPhoneAndDeletedFalse(request.getPhone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already exists.");
        }

        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .deleted(false)
                .role(Role.CUSTOMER)
                .build();

        return mapToResponse(customerRepository.save(customer));
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return mapToResponse(customer);
    }

    @Override
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findByDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        return mapToResponse(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public void softDeleteCustomer(Long id) {
        Customer customer = customerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        customerRepository.softDeleteCustomer(id);
    }

    @Override
    public CustomerResponse getCustomerByPhone(String phone) {
        Customer customer = customerRepository.findByPhoneAndDeletedFalse(phone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return mapToResponse(customer);
    }

    @Override
    public Page<CustomerResponse> searchCustomers(String keyword, Pageable pageable) {
        return customerRepository.searchCustomers(keyword, pageable)
                .map(this::mapToResponse);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress()
        );
    }
}
