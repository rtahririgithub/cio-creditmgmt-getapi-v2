package com.telus.credit.service;

import java.util.Optional;

import com.telus.credit.model.Customer;

public interface CustomerService {

    Optional<Customer> findCustomerFromStore(String custId, String lang);

    Customer getCurrentAssesmentCodes(Customer customer);

}