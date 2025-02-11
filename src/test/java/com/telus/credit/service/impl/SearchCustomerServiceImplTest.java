package com.telus.credit.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.telus.credit.firestore.AssessmentCollectionService;
import com.telus.credit.firestore.CustomerCollectionService;
import com.telus.credit.model.Customer;
import com.telus.credit.model.Individual;
import com.telus.credit.model.RelatedParty;
import com.telus.credit.model.TelusCreditProfile;
import com.telus.credit.model.TelusIndividualIdentification;
import io.micrometer.core.instrument.util.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.reflect.Whitebox;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.telus.credit.model.common.ApplicationConstants.CUSTOMER_ID_KEY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SearchCustomerServiceImplTest {

    @InjectMocks
    private SearchCustomerServiceImpl searchCustomerService;

    @Mock
    private CustomerCollectionService readDB;

    @Mock
    private AssessmentCollectionService assesmentDB;

    @Mock
    private ResponseInterceptorService customerEnrichmentService;

    @Test
    public void testSearchCustomerInStore( ){
        Whitebox.setInternalState(searchCustomerService, "readDB", readDB);
        MultiValueMap<String, String> orParams = new LinkedMultiValueMap<>();
        orParams.add("DL", "212");
        orParams.add("SIN", "5568797");
        MultiValueMap<String, String> andParams = new LinkedMultiValueMap<>();
        andParams.add(CUSTOMER_ID_KEY, "5568797");

        Map<String, MultiValueMap<String, String>> searchParamsMap = new HashMap<>();
        searchParamsMap.put("AND", andParams);
        searchParamsMap.put("OR", orParams);

        MultiValueMap<String, String> searchParams = new LinkedMultiValueMap<>();
        searchParams.addAll(orParams);
        searchParams.addAll(orParams);
        String lang = "en";
        List<Customer> resultCustomers = new LinkedList<>();
        resultCustomers.add(getCustomer("customer1-same-dl.json"));
        List<Customer> resultCustomers1 = new LinkedList<>();
        resultCustomers1.add(getCustomer("customer2-same-dl.json"));
        Optional<List<Customer>> optionalCustomers = Optional.of(resultCustomers);
        Optional<List<Customer>> optionalCustomers1 = Optional.of(resultCustomers1);
        when(readDB.searchDocument(any(), anyBoolean())).thenReturn(optionalCustomers, optionalCustomers1);
        Optional<List<Customer>> customersOptional = searchCustomerService.searchCustomerInStore(searchParamsMap, lang);
        Assertions.assertTrue(customersOptional.isPresent());
        Assertions.assertEquals(2, customersOptional.get().size() );
    }

    private Customer getCustomer(String customerId) {
        TelusIndividualIdentification telusIndividualIdentification = new TelusIndividualIdentification();
        telusIndividualIdentification.setIdentificationId("DL123455");
        telusIndividualIdentification.setIdentificationType("DL");
        List<TelusIndividualIdentification> telusIndividualIdentifications = new ArrayList<>();
        telusIndividualIdentifications.add(telusIndividualIdentification);
        Individual individual = new Individual();
        individual.setIndividualIdentification(telusIndividualIdentifications);
        RelatedParty relatedParty = new RelatedParty();
        relatedParty.setId(customerId);
        relatedParty.setRole( "customer");
        relatedParty.setIndividual(individual);
        TelusCreditProfile creditProfile = new TelusCreditProfile();
        creditProfile.setRelatedParty(Lists.newArrayList(relatedParty));
        Customer customer = new Customer();
        customer.setCreditProfile(Lists.newArrayList(creditProfile));
        customer.setId(customerId);
        return customer;
    }

    private Customer getCustomer(String file, int a){
        String json1 = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(file), StandardCharsets.UTF_8);
        try {
            return new ObjectMapper().readValue(json1, Customer.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
