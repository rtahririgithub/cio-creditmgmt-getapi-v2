package com.telus.credit.service.impl;

import com.telus.credit.common.RequestContext;
import com.telus.credit.exceptions.CreditException;
import com.telus.credit.exceptions.ExceptionConstants;
import com.telus.credit.firestore.AssessmentCollectionService;
import com.telus.credit.firestore.CustomerCollectionService;
import com.telus.credit.model.Customer;
import com.telus.credit.model.TelusAuditCharacteristic;
import com.telus.credit.model.TelusCreditProfile;
import com.telus.credit.service.CreditProfileService;
import com.telus.credit.service.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class DefaultCreditProfileServiceImpl implements CreditProfileService {

    private CustomerCollectionService readDB;

    private ResponseInterceptorService responseInterceptorService;
    
	@Autowired
	private CustomerService customerService;

    public DefaultCreditProfileServiceImpl(CustomerCollectionService readDB,
                                  AssessmentCollectionService assesmentDB,
                                  ResponseInterceptorService responseInterceptorService) {
        this.readDB = readDB;
        this.responseInterceptorService = responseInterceptorService;
    }

    @Override
    public void createCreditProfile(String customerUid, TelusCreditProfile creditProfile, TelusAuditCharacteristic auditCharacteristic) {

    }

    @Override
    public void patchCreditProfile(String customerUid, TelusCreditProfile creditProfile, TelusAuditCharacteristic auditCharacteristic) {

    }

    @Override
    public List getCreditProfiles(String customerUid) {
        List<Customer> customerList = new ArrayList<>();
        Optional<Customer> optionalData = readDB.findDocument(customerUid);

        if (optionalData.isPresent()) {
            Customer customer = optionalData.get();
            customerList.add(customer);
        }

        customerList.forEach(System.out::println);
        return customerList;
    }

    @Override
    public List<TelusCreditProfile> getCreditProfile(RequestContext context, String creditProfileId) throws ExecutionException, InterruptedException {
        List<Customer> customerList = new ArrayList<>();
        Optional<Customer> optionalData = readDB.findCreditProfileDocument(creditProfileId);

        Customer customer = null;
        if (optionalData.isPresent()) {
            customer = optionalData.get();
            customer = customerService.getCurrentAssesmentCodes(customer);
            enrichData(customer, context.getAcceptLang());
            customerList.add(customer);
        }


        if (!optionalData.isPresent()) {
            throw new CreditException(HttpStatus.NOT_FOUND, ExceptionConstants.ERR_CODE_1402, ExceptionConstants.ERR_CODE_1402_MSG, "CREDIT PROFILE NOT FOUND");
        }

        List<TelusCreditProfile> creditProfiles = customer.getCreditProfile();
        return creditProfiles;
    }

    private void enrichData(Customer customer, String lang) {
        responseInterceptorService.resolveMissingFields(customer, lang);
    }
}
