package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.CustomerRequest;
import com.cab.chaloCab.dto.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse getCustomerById(Long id);

    Page<CustomerResponse> getAllCustomers(Pageable pageable);

    CustomerResponse updateCustomer(Long id, CustomerRequest request);

    void deleteCustomer(Long id);

    void softDeleteCustomer(Long id);

    CustomerResponse getCustomerByPhone(String phone);

    Page<CustomerResponse> searchCustomers(String keyword, Pageable pageable);
}
