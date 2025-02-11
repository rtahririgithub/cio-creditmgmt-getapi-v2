/*
package com.telus.credit.controllers;

import com.telus.credit.common.RequestContext;
import com.telus.credit.controllers.CreditProfileController;
import com.telus.credit.model.Customer;
import com.telus.credit.model.Individual;
import com.telus.credit.model.RelatedParty;
import com.telus.credit.model.TelusCharacteristic;
import com.telus.credit.model.TelusCreditProfile;
import com.telus.credit.service.CreditProfileService;
import com.telus.credit.service.CustomerService;
import com.telus.credit.service.SearchCustomerService;
import com.telus.credit.service.ValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CreditProfileControllerTest {
    private static final String POPULATE_METHOD_CD = "POPULATE_METHOD_CD";
    private static final String CPROFL_FORMAT_CD = "CPROFL_FORMAT_CD";
    private static final String COMMENT_TXT = "COMMENT_TXT";
    private static final String BUS_LAST_UPDT_TS = "BUS_LAST_UPDT_TS";
    private static final String BYPASS_MATCH_IND = "BYPASS_MATCH_IND";
    private static final String POPULATE_METHOD_CD_VALUE = "TestMethodCode";
    private static final String CPROFL_FORMAT_CD_VALUE = "FormatCode";
    private static final String COMMENT_TXT_VALUE = "Comment";
    private static final String BUS_LAST_UPDT_TS_VALUE = "date-time";
    private static final String BYPASS_MATCH_IND_VALUE = "false";


    private static final String EMPLOYMENT_STATUS_CD = "EMPLOYMENT_STATUS_CD";
    private static final String PRIM_CRED_CARD_TYP_CD = "PRIM_CRED_CARD_TYP_CD";
    private static final String RESIDENCY_CD = "RESIDENCY_CD";
    private static final String SEC_CRED_CARD_ISS_CO_TYP_CD = "SEC_CRED_CARD_ISS_CO_TYP_CD";
    private static final String EMPLOYMENT_STATUS_CD_VALUE = "SampleStatusCode";
    private static final String PRIM_CRED_CARD_TYP_CD_VALUE = "TestCardTypeCode";
    private static final String RESIDENCY_CD_VALUE = "nj";
    private static final String SEC_CRED_CARD_ISS_CO_TYP_CD_VALUE = "TestCodTypeCode";

    @InjectMocks
    private CreditProfileController creditProfileController;

    @Mock
    private CustomerService customerService;

    @Mock
    private ValidationService validationService;

    @Mock
    private SearchCustomerService searchService;

    @Mock
    private CreditProfileService creditProfileService;

    @Mock
    private RequestContext requestContext;

    @Test
    public void testSearchCreditProfile_withoutCharacteristics_atIndividualAndCreditProfileLevel() {
        Individual individual = new Individual();
        individual.setId("123455");
        RelatedParty relatedParty = new RelatedParty();
        relatedParty.setId("12345");
        relatedParty.setRole("Customer");
        relatedParty.setType("Individual");
        relatedParty.setIndividual(individual);
        List<RelatedParty> relatedParties = new LinkedList<>();
        relatedParties.add(relatedParty);
        TelusCreditProfile creditProfile = new TelusCreditProfile();
        creditProfile.setRelatedParty(relatedParties);
        List<TelusCreditProfile> creditProfiles = new LinkedList<>();
        creditProfiles.add(creditProfile);
        Customer customer = new Customer();
        customer.setId("12571547");
        customer.setCreditProfile(creditProfiles);
        List<Customer> customers = new LinkedList<>();
        customers.add(customer);
        when(requestContext.getAcceptLang()).thenReturn("en");
        when(searchService.searchCustomerInStore(any(), anyString(), any())).thenReturn(Optional.of(customers));
        List<TelusCreditProfile> telusCreditProfiles = creditProfileController.searchCreditProfile(requestContext, "customerId=100");
        assertThat(telusCreditProfiles).hasSize(1);
        List<TelusCharacteristic> characteristics = telusCreditProfiles.get(0).getCharacteristics();
        assertThat(characteristics).isNullOrEmpty();

        List<RelatedParty> relatedPartyList = telusCreditProfiles.get(0).getRelatedParty();
        assertThat(relatedPartyList).hasSize(1);
        List<TelusCharacteristic> individualCharacteristics = relatedPartyList.get(0).getIndividual().getCharacteristics();
        assertThat(individualCharacteristics).isNullOrEmpty();

    }

    @Test
    public void testSearchCreditProfile_withCharacteristics_atIndividualAndCreditProfileLevel() {
        Individual individual = new Individual();
        individual.setId("123455");
        individual.setCharacteristics(getIndividualCharacteristics());
        RelatedParty relatedParty = new RelatedParty();
        relatedParty.setId("12345");
        relatedParty.setRole("Customer");
        relatedParty.setType("Individual");
        relatedParty.setIndividual(individual);
        List<RelatedParty> relatedParties = new LinkedList<>();
        relatedParties.add(relatedParty);
        TelusCreditProfile creditProfile = new TelusCreditProfile();
        creditProfile.setRelatedParty(relatedParties);
        creditProfile.setCharacteristics(getCreditProfileCharacteristics());
        List<TelusCreditProfile> creditProfiles = new LinkedList<>();
        creditProfiles.add(creditProfile);
        Customer customer = new Customer();
        customer.setId("12571547");
        customer.setCreditProfile(creditProfiles);
        List<Customer> customers = new LinkedList<>();
        customers.add(customer);
        when(requestContext.getAcceptLang()).thenReturn("en");
        when(searchService.searchCustomerInStore(any(), anyString(), any())).thenReturn(Optional.of(customers));
        List<TelusCreditProfile> telusCreditProfiles = creditProfileController.searchCreditProfile(requestContext, "customerId=100");
        assertThat(telusCreditProfiles).hasSize(1);
        List<TelusCharacteristic> characteristics = telusCreditProfiles.get(0).getCharacteristics();
        assertThat(characteristics).hasSize(5);
        assertThat(characteristics.get(0).getName()).isEqualTo(POPULATE_METHOD_CD);
        assertThat(characteristics.get(0).getValue()).isEqualTo(POPULATE_METHOD_CD_VALUE);
        assertThat(characteristics.get(1).getName()).isEqualTo(CPROFL_FORMAT_CD);
        assertThat(characteristics.get(1).getValue()).isEqualTo(CPROFL_FORMAT_CD_VALUE);
        assertThat(characteristics.get(2).getName()).isEqualTo(COMMENT_TXT);
        assertThat(characteristics.get(2).getValue()).isEqualTo(COMMENT_TXT_VALUE);
        assertThat(characteristics.get(3).getName()).isEqualTo(BUS_LAST_UPDT_TS);
        assertThat(characteristics.get(3).getValue()).isEqualTo(BUS_LAST_UPDT_TS_VALUE);
        assertThat(characteristics.get(4).getName()).isEqualTo(BYPASS_MATCH_IND);
        assertThat(characteristics.get(4).getValue()).isEqualTo(BYPASS_MATCH_IND_VALUE);

        List<RelatedParty> relatedPartyList = telusCreditProfiles.get(0).getRelatedParty();
        assertThat(relatedPartyList).hasSize(1);
        List<TelusCharacteristic> individualCharacteristics = relatedPartyList.get(0).getIndividual().getCharacteristics();
        assertThat(individualCharacteristics).hasSize(5);
        assertThat(individualCharacteristics.get(0).getName()).isEqualTo(EMPLOYMENT_STATUS_CD);
        assertThat(individualCharacteristics.get(0).getValue()).isEqualTo(EMPLOYMENT_STATUS_CD_VALUE);
        assertThat(individualCharacteristics.get(1).getName()).isEqualTo(PRIM_CRED_CARD_TYP_CD);
        assertThat(individualCharacteristics.get(1).getValue()).isEqualTo(PRIM_CRED_CARD_TYP_CD_VALUE);
        assertThat(individualCharacteristics.get(2).getName()).isEqualTo(RESIDENCY_CD);
        assertThat(individualCharacteristics.get(2).getValue()).isEqualTo(RESIDENCY_CD_VALUE);
        assertThat(individualCharacteristics.get(3).getName()).isEqualTo(SEC_CRED_CARD_ISS_CO_TYP_CD);
        assertThat(individualCharacteristics.get(3).getValue()).isEqualTo(SEC_CRED_CARD_ISS_CO_TYP_CD_VALUE);
        assertThat(individualCharacteristics.get(4).getName()).isEqualTo(BYPASS_MATCH_IND);
        assertThat(individualCharacteristics.get(4).getValue()).isEqualTo(BYPASS_MATCH_IND_VALUE);

    }

    @Test
    public void testSearchCreditProfile_withCharacteristics_atCreditProfileLevel() {
        Individual individual = new Individual();
        individual.setId("123455");
        RelatedParty relatedParty = new RelatedParty();
        relatedParty.setId("12345");
        relatedParty.setRole("Customer");
        relatedParty.setType("Individual");
        relatedParty.setIndividual(individual);
        List<RelatedParty> relatedParties = new LinkedList<>();
        relatedParties.add(relatedParty);
        TelusCreditProfile creditProfile = new TelusCreditProfile();
        creditProfile.setRelatedParty(relatedParties);
        creditProfile.setCharacteristics(getCreditProfileCharacteristics());
        List<TelusCreditProfile> creditProfiles = new LinkedList<>();
        creditProfiles.add(creditProfile);
        Customer customer = new Customer();
        customer.setId("12571547");
        customer.setCreditProfile(creditProfiles);
        List<Customer> customers = new LinkedList<>();
        customers.add(customer);
        when(requestContext.getAcceptLang()).thenReturn("en");
        when(searchService.searchCustomerInStore(any(), anyString(), any())).thenReturn(Optional.of(customers));
        List<TelusCreditProfile> telusCreditProfiles = creditProfileController.searchCreditProfile(requestContext, "customerId=100");
        assertThat(telusCreditProfiles).hasSize(1);
        List<TelusCharacteristic> characteristics = telusCreditProfiles.get(0).getCharacteristics();
        assertThat(characteristics).hasSize(5);
        assertThat(characteristics.get(0).getName()).isEqualTo(POPULATE_METHOD_CD);
        assertThat(characteristics.get(0).getValue()).isEqualTo(POPULATE_METHOD_CD_VALUE);
        assertThat(characteristics.get(1).getName()).isEqualTo(CPROFL_FORMAT_CD);
        assertThat(characteristics.get(1).getValue()).isEqualTo(CPROFL_FORMAT_CD_VALUE);
        assertThat(characteristics.get(2).getName()).isEqualTo(COMMENT_TXT);
        assertThat(characteristics.get(2).getValue()).isEqualTo(COMMENT_TXT_VALUE);
        assertThat(characteristics.get(3).getName()).isEqualTo(BUS_LAST_UPDT_TS);
        assertThat(characteristics.get(3).getValue()).isEqualTo(BUS_LAST_UPDT_TS_VALUE);
        assertThat(characteristics.get(4).getName()).isEqualTo(BYPASS_MATCH_IND);
        assertThat(characteristics.get(4).getValue()).isEqualTo(BYPASS_MATCH_IND_VALUE);

        List<RelatedParty> relatedPartyList = telusCreditProfiles.get(0).getRelatedParty();
        assertThat(relatedPartyList).hasSize(1);
        List<TelusCharacteristic> individualCharacteristics = relatedPartyList.get(0).getIndividual().getCharacteristics();
        assertThat(individualCharacteristics).isNullOrEmpty();

    }

    @Test
    public void testSearchCreditProfile_withCharacteristics_atIndividualLevel() {
        Individual individual = new Individual();
        individual.setId("123455");
        individual.setCharacteristics(getIndividualCharacteristics());
        RelatedParty relatedParty = new RelatedParty();
        relatedParty.setId("12345");
        relatedParty.setRole("Customer");
        relatedParty.setType("Individual");
        relatedParty.setIndividual(individual);
        List<RelatedParty> relatedParties = new LinkedList<>();
        relatedParties.add(relatedParty);
        TelusCreditProfile creditProfile = new TelusCreditProfile();
        creditProfile.setRelatedParty(relatedParties);
        List<TelusCreditProfile> creditProfiles = new LinkedList<>();
        creditProfiles.add(creditProfile);
        Customer customer = new Customer();
        customer.setId("12571547");
        customer.setCreditProfile(creditProfiles);
        List<Customer> customers = new LinkedList<>();
        customers.add(customer);
        when(requestContext.getAcceptLang()).thenReturn("en");
        when(searchService.searchCustomerInStore(any(), anyString(), any())).thenReturn(Optional.of(customers));
        List<TelusCreditProfile> telusCreditProfiles = creditProfileController.searchCreditProfile(requestContext, "customerId=100");
        assertThat(telusCreditProfiles).hasSize(1);
        List<TelusCharacteristic> characteristics = telusCreditProfiles.get(0).getCharacteristics();
        assertThat(characteristics).isNullOrEmpty();

        List<RelatedParty> relatedPartyList = telusCreditProfiles.get(0).getRelatedParty();
        assertThat(relatedPartyList).hasSize(1);
        List<TelusCharacteristic> individualCharacteristics = relatedPartyList.get(0).getIndividual().getCharacteristics();
        assertThat(individualCharacteristics).hasSize(5);
        assertThat(individualCharacteristics.get(0).getName()).isEqualTo(EMPLOYMENT_STATUS_CD);
        assertThat(individualCharacteristics.get(0).getValue()).isEqualTo(EMPLOYMENT_STATUS_CD_VALUE);
        assertThat(individualCharacteristics.get(1).getName()).isEqualTo(PRIM_CRED_CARD_TYP_CD);
        assertThat(individualCharacteristics.get(1).getValue()).isEqualTo(PRIM_CRED_CARD_TYP_CD_VALUE);
        assertThat(individualCharacteristics.get(2).getName()).isEqualTo(RESIDENCY_CD);
        assertThat(individualCharacteristics.get(2).getValue()).isEqualTo(RESIDENCY_CD_VALUE);
        assertThat(individualCharacteristics.get(3).getName()).isEqualTo(SEC_CRED_CARD_ISS_CO_TYP_CD);
        assertThat(individualCharacteristics.get(3).getValue()).isEqualTo(SEC_CRED_CARD_ISS_CO_TYP_CD_VALUE);
        assertThat(individualCharacteristics.get(4).getName()).isEqualTo(BYPASS_MATCH_IND);
        assertThat(individualCharacteristics.get(4).getValue()).isEqualTo(BYPASS_MATCH_IND_VALUE);

    }

    @Test
    public void testSearchCreditProfile_withoutCharacteristics() {
        Individual individual = new Individual();
        individual.setId("123455");
        RelatedParty relatedParty = new RelatedParty();
        relatedParty.setId("12345");
        relatedParty.setRole("Customer");
        relatedParty.setType("Individual");
        relatedParty.setIndividual(individual);
        List<RelatedParty> relatedParties = new LinkedList<>();
        relatedParties.add(relatedParty);
        TelusCreditProfile creditProfile = new TelusCreditProfile();
        creditProfile.setRelatedParty(relatedParties);
        List<TelusCreditProfile> creditProfiles = new LinkedList<>();
        creditProfiles.add(creditProfile);
        Customer customer = new Customer();
        customer.setId("12571547");
        customer.setCreditProfile(creditProfiles);
        List<Customer> customers = new LinkedList<>();
        customers.add(customer);
        when(requestContext.getAcceptLang()).thenReturn("en");
        when(searchService.searchCustomerInStore(any(), anyString(), any())).thenReturn(Optional.of(customers));
        List<TelusCreditProfile> telusCreditProfiles = creditProfileController.searchCreditProfile(requestContext, "customerId=100");
        assertThat(telusCreditProfiles).hasSize(1);
        List<TelusCharacteristic> characteristics = telusCreditProfiles.get(0).getCharacteristics();
        assertThat(characteristics).isNullOrEmpty();

        List<RelatedParty> relatedPartyList = telusCreditProfiles.get(0).getRelatedParty();
        assertThat(relatedPartyList).hasSize(1);
        List<TelusCharacteristic> individualCharacteristics = relatedPartyList.get(0).getIndividual().getCharacteristics();
        assertThat(individualCharacteristics).isNullOrEmpty();

    }

    private List<TelusCharacteristic> getIndividualCharacteristics() {
        TelusCharacteristic characteristic1 = new TelusCharacteristic();
        characteristic1.setName(EMPLOYMENT_STATUS_CD);
        characteristic1.setValue(EMPLOYMENT_STATUS_CD_VALUE);

        TelusCharacteristic characteristic2 = new TelusCharacteristic();
        characteristic2.setName(PRIM_CRED_CARD_TYP_CD);
        characteristic2.setValue(PRIM_CRED_CARD_TYP_CD_VALUE);

        TelusCharacteristic characteristic3 = new TelusCharacteristic();
        characteristic3.setName(RESIDENCY_CD);
        characteristic3.setValue(RESIDENCY_CD_VALUE);

        TelusCharacteristic characteristic4 = new TelusCharacteristic();
        characteristic4.setName(SEC_CRED_CARD_ISS_CO_TYP_CD);
        characteristic4.setValue(SEC_CRED_CARD_ISS_CO_TYP_CD_VALUE);

        TelusCharacteristic characteristic5 = new TelusCharacteristic();
        characteristic5.setName(BYPASS_MATCH_IND);
        characteristic5.setValue(BYPASS_MATCH_IND_VALUE);

        List<TelusCharacteristic> characteristics = new LinkedList<>();
        characteristics.add(characteristic1);
        characteristics.add(characteristic2);
        characteristics.add(characteristic3);
        characteristics.add(characteristic4);
        characteristics.add(characteristic5);
        return characteristics;
    }

    private List<TelusCharacteristic> getCreditProfileCharacteristics() {
        TelusCharacteristic characteristic1 = new TelusCharacteristic();
        characteristic1.setName(POPULATE_METHOD_CD);
        characteristic1.setValue(POPULATE_METHOD_CD_VALUE);

        TelusCharacteristic characteristic2 = new TelusCharacteristic();
        characteristic2.setName(CPROFL_FORMAT_CD);
        characteristic2.setValue(CPROFL_FORMAT_CD_VALUE);

        TelusCharacteristic characteristic3 = new TelusCharacteristic();
        characteristic3.setName(COMMENT_TXT);
        characteristic3.setValue(COMMENT_TXT_VALUE);

        TelusCharacteristic characteristic4 = new TelusCharacteristic();
        characteristic4.setName(BUS_LAST_UPDT_TS);
        characteristic4.setValue(BUS_LAST_UPDT_TS_VALUE);

        TelusCharacteristic characteristic5 = new TelusCharacteristic();
        characteristic5.setName(BYPASS_MATCH_IND);
        characteristic5.setValue(BYPASS_MATCH_IND_VALUE);

        List<TelusCharacteristic> characteristics = new LinkedList<>();
        characteristics.add(characteristic1);
        characteristics.add(characteristic2);
        characteristics.add(characteristic3);
        characteristics.add(characteristic4);
        characteristics.add(characteristic5);
        return characteristics;
    }
}

*/