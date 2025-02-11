package com.telus.credit.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.MultiValueMap;

import com.telus.credit.model.Customer;

public interface SearchCustomerService {

	Optional<List<Customer>> searchCustomerInStore(Map<String, MultiValueMap<String,String>> searchParams, String lang);
}
