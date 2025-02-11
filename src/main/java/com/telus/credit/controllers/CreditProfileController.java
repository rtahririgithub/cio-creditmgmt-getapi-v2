package com.telus.credit.controllers;


import com.telus.credit.common.CommonHelper;
import com.telus.credit.common.RequestContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import com.telus.credit.common.CommonHelper;

import com.telus.credit.model.CreditProfile;
import com.telus.credit.model.Customer;
import com.telus.credit.model.RelatedParty;
import com.telus.credit.model.RiskLevelRiskAssessment;
import com.telus.credit.model.TelusCreditDecisionWarning;
import com.telus.credit.model.TelusCreditProfile;
import com.telus.credit.service.CreditProfileService;
import com.telus.credit.service.CustomerService;
import com.telus.credit.service.SearchCustomerService;
import com.telus.credit.service.ValidationService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.telus.credit.common.RequestContext;
import com.telus.credit.model.Customer;
import com.telus.credit.model.RelatedParty;
import com.telus.credit.service.SearchCustomerService;
import com.telus.credit.service.ValidationService;


@Component
public class CreditProfileController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreditProfileController.class);

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ValidationService validationService;

	@Autowired
	private SearchCustomerService searchService;

	@Autowired
	private CreditProfileService creditProfileService;

	/*
		public Customer getCustomerById(RequestContext requestContext, String custId) {
			MDC.put("debugContext", "CustId=" + custId);
			LOGGER.info("start getCustomerById");
			Optional<Customer> cust = customerService.findCustomerFromStore(custId, requestContext.getAcceptLang());

			if (!cust.isPresent()) {
				//throw new CreditException(HttpStatus.NOT_FOUND, ExceptionConstants.ERR_CODE_1400,ExceptionConstants.ERR_CODE_1400_MSG, "CUSTOMER NOT FOUND", ("CustId=" + custId) );
				LOGGER.warn( "CUSTOMER NOT FOUND CustId=" + custId);
				Customer emptycust = new Customer();
				emptycust.setId(custId);
				return emptycust;
			}

			Customer customer = mapLegacyCustomer(cust.get());
			return customer;
		}
	*/
	private Customer mapV1_to_V2_Schema(Customer firestoreCustomer) {
		Customer updatedCustomer = new Customer();
		List<TelusCreditProfile> v2_CreditProfiles = new ArrayList<TelusCreditProfile>();
		updatedCustomer.setBaseType(StringUtils.defaultIfEmpty(firestoreCustomer.getBaseType(), ""));

		if (firestoreCustomer.getCreditProfile().size() > 0) {
			//Creditprofile with TelusCharacteristic are V1 schema
			if (ObjectUtils.isNotEmpty(firestoreCustomer.getCreditProfile().get(0).getTelusCharacteristic())) {
				for (TelusCreditProfile creditProfileV1 : firestoreCustomer.getCreditProfile()) {
					TelusCreditProfile creditProfileV2 = convertCreditProfile_V1_to_V2(firestoreCustomer, creditProfileV1);
					v2_CreditProfiles.add(creditProfileV2);
				}
				updatedCustomer.setCreditProfile(v2_CreditProfiles);
				return updatedCustomer;
			}
		}
		return firestoreCustomer;
	}

	private TelusCreditProfile convertCreditProfile_V1_to_V2(Customer customer, TelusCreditProfile creditProfileV1) {
		String customerId = customer.getId();
		TelusCreditProfile creditProfileV2 = new TelusCreditProfile();

		creditProfileV2.setId(creditProfileV1.getId());
		creditProfileV2.setAttachments(creditProfileV1.getAttachments());
		creditProfileV2.setCreditClassCd(creditProfileV1.getTelusCharacteristic().getCreditClassCd());

		List<TelusCreditDecisionWarning> warningsV1 = creditProfileV1.getTelusCharacteristic().getWarningHistoryList();
		for (TelusCreditDecisionWarning telusCreditDecisionWarning : warningsV1) {
			telusCreditDecisionWarning.setId(telusCreditDecisionWarning.getWarningHistoryId());
			telusCreditDecisionWarning.setWarningDetectionTs((telusCreditDecisionWarning.getWarningDetectionDate()));
		}
		creditProfileV2.setWarnings(creditProfileV1.getTelusCharacteristic().getWarningHistoryList());


		creditProfileV2.setCreationTs(creditProfileV1.getCreationTs());
		creditProfileV2.setValidFor(creditProfileV1.getValidFor());
		creditProfileV2.setBaseType(creditProfileV1.getBaseType());
		creditProfileV2.setType(creditProfileV1.getType());


		creditProfileV2.setCreditRiskLevelDecisionCd(creditProfileV1.getTelusCharacteristic().getRiskLevelDecisionCd());
		creditProfileV2.setClpCreditLimitAmt(creditProfileV1.getTelusCharacteristic().getClpCreditLimit());
		creditProfileV2.setClpContractTermNum(creditProfileV1.getTelusCharacteristic().getClpContractTerm());
		creditProfileV2.setClpRatePlanAmt(creditProfileV1.getTelusCharacteristic().getClpRatePlanAmt());
		creditProfileV2.setCreditRiskLevelTs(creditProfileV1.getTelusCharacteristic().getRiskLevelDt());


		creditProfileV2.setPrimaryCreditScoreCd(creditProfileV1.getTelusCharacteristic().getPrimaryCreditScoreCd());
		creditProfileV2.setPrimaryCreditScoreTypeCd(creditProfileV1.getTelusCharacteristic().getPrimaryCreditScoreTypeCd());

		creditProfileV2.setBureauDecisionCd(creditProfileV1.getTelusCharacteristic().getBureauDecisionCode());
		creditProfileV2.setBureauDecisionCdTxtEn(creditProfileV1.getTelusCharacteristic().getBureauDecisionMessage());
		creditProfileV2.setBureauDecisionCdTxtFr(creditProfileV1.getTelusCharacteristic().getBureauDecisionMessage_fr());
		creditProfileV2.setCreditProgramName(creditProfileV1.getTelusCharacteristic().getCreditProgramName());
		creditProfileV2.setCreditClassCd(creditProfileV1.getTelusCharacteristic().getCreditClassCd());
		creditProfileV2.setCreditClassTs(creditProfileV1.getTelusCharacteristic().getCreditClassDate());
		creditProfileV2.setCreditDecisionCd(creditProfileV1.getTelusCharacteristic().getCreditDecisionCd());
		creditProfileV2.setCreditDecisionTs(creditProfileV1.getTelusCharacteristic().getCreditDecisionDate());


		String riskLevelNumberV1 = creditProfileV1.getTelusCharacteristic().getRiskLevelNumber();
		riskLevelNumberV1 = (riskLevelNumberV1 != null && !riskLevelNumberV1.equalsIgnoreCase("null")) ? riskLevelNumberV1.trim() : "";
		riskLevelNumberV1 = (riskLevelNumberV1.isEmpty()) ? creditProfileV1.getCreditRiskRating() : riskLevelNumberV1;
		creditProfileV2.setCreditRiskLevelNum(riskLevelNumberV1);

		creditProfileV2.setCreditRiskLevelTs(creditProfileV1.getTelusCharacteristic().getRiskLevelDt());
		creditProfileV2.setCreditRiskLevelDecisionCd(creditProfileV1.getTelusCharacteristic().getRiskLevelDecisionCd());
		creditProfileV2.setClpCreditLimitAmt(creditProfileV1.getTelusCharacteristic().getClpCreditLimit());
		creditProfileV2.setAverageSecurityDepositAmt(creditProfileV1.getTelusCharacteristic().getAverageSecurityDepositAmt());

		RiskLevelRiskAssessment lastRiskAssessment = new RiskLevelRiskAssessment();
		lastRiskAssessment.setAssessmentMessageCd(creditProfileV1.getTelusCharacteristic().getAssessmentMessageCode());
		lastRiskAssessment.setAssessmentMessageTxtEn(creditProfileV1.getTelusCharacteristic().getAssessmentMessage());
		lastRiskAssessment.setAssessmentMessageTxtFr(creditProfileV1.getTelusCharacteristic().getAssessmentMessage_fr());
		lastRiskAssessment.setId(creditProfileV1.getTelusCharacteristic().getCreditAssessmentId());

		creditProfileV2.setLastRiskAssessment(lastRiskAssessment);

		RiskLevelRiskAssessment riskLevelRiskAssessment = new RiskLevelRiskAssessment();
		riskLevelRiskAssessment.setId(creditProfileV1.getTelusCharacteristic().getCreditAssessmentId());
		creditProfileV2.setRiskLevelRiskAssessment(riskLevelRiskAssessment);

		//populate RelatedParty and engaged party
		{
			RelatedParty customerRelatedParty = new RelatedParty();
			customerRelatedParty.setId(customerId);
			customerRelatedParty.setRole("customer");
			customerRelatedParty.setType("Customer");
			if (customer.getIndividual() != null) {
				customerRelatedParty.setIndividual(customer.getIndividual());
				creditProfileV2.setCharacteristic(creditProfileV1.getCharacteristic());
			} else {
				if (customer.getOrganisation() != null) {
					customerRelatedParty.setOrganization((customer.getOrganisation()));
				}
			}
			List<RelatedParty> relatedParties = new ArrayList<RelatedParty>();
			relatedParties.add(customerRelatedParty);
			creditProfileV2.setRelatedParty(relatedParties);
		}
		creditProfileV2.setTelusCharacteristic(null);
		return creditProfileV2;
	}

	public List<Customer> searchCustomer(RequestContext requestContext, MultiValueMap<String, String> searchParams) {
		validationService.validSearchParamsPresent(searchParams);
		Map<String, MultiValueMap<String, String>> searchParamsMap = new HashMap<>();
		searchParamsMap.put("AND", searchParams);
		Optional<List<Customer>> result = searchService.searchCustomerInStore(searchParamsMap, requestContext.getAcceptLang());
		if (result.isPresent()) {
			return result.get();
		}
		LOGGER.debug("No results found");
		return Collections.emptyList();
	}
// Requirement: return the primary creditprofile for the search params containing customerId
	//search logic :	
	//	Search in firestore for  documents with matching customerId
	//	there should be only a single document for a given customerId. ( in case multiple documents are found, select the latest)
	//  From the select document , return only the primary credit profile

// Requirement: return list of primary creditprofiles matching the search params not containing customerId ( such as Credit Identification,..)
	// Note: 
	//	A document in firestore represent list of all credit profiles associated to a customerID. 
	//	a customer can have multiple credirpofile but only one primary 
	//search logic :
	//	Search in firestore for  documents with matching params
	//	From each document , select only the primary credit profile.
	//	Return list of all primary creditprofiles


	public List<TelusCreditProfile> searchCreditProfile(RequestContext requestContext, String queryString) {
		Map<String, MultiValueMap<String, String>> searchParamsMap = getQueryParameters(queryString);
		MultiValueMap<String, String> searchParams = new LinkedMultiValueMap<>();
		if (searchParamsMap.containsKey("AND")) {
			searchParams.addAll(searchParamsMap.get("AND"));
		}
		if (searchParamsMap.containsKey("OR")) {
			searchParams.addAll(searchParamsMap.get("OR"));
		}
		validationService.validSearchParamsPresent(searchParams);
		LOGGER.info("start searchCreditProfile for searchParams:" + searchParams);
		//search for list of all customer document in firestore matching the search params.
		Optional<List<Customer>> customerListSearchResult = searchService.searchCustomerInStore(searchParamsMap, requestContext.getAcceptLang());

		List<Customer> optionalCustomers;
		if (customerListSearchResult.isPresent()) {
			//convert document in version1 schema to version2
			optionalCustomers =
					CommonHelper.nullSafe(customerListSearchResult.get())
							.stream()
							.map(customer -> {
								Customer customer1 = mapV1_to_V2_Schema(customer);
								return customer1;
							})
							.collect(Collectors.toList());

			//if search params contain customerId , return single customer 
			/*if (searchParams.get("customerId") != null) {
				Customer customer = optionalCustomers.get(0);
				List<TelusCreditProfile> creditProfiles = customer.getCreditProfile();
				return creditProfiles;
			} else {*/
			List<TelusCreditProfile> allCustomersCreditProfiles = new ArrayList<>();
			for (Customer customer : optionalCustomers) {
				allCustomersCreditProfiles.addAll(customer.getCreditProfile());
			}
			return allCustomersCreditProfiles;
			//}
		}
		LOGGER.debug("No results found");
		return Collections.emptyList();
	}

	public List<CreditProfile> getCreditProfileByCustId(String custId) {
		MDC.put("debugContext", "CustId=" + custId);

		List<CreditProfile> creditProfiles = creditProfileService.getCreditProfiles(custId);

		if (creditProfiles.isEmpty()) {
			LOGGER.warn("CREDIT PROFILE NOT FOUND FOR CustId=" + custId);
			CreditProfile creditProfile = new CreditProfile();
			creditProfile.setId(custId);
			creditProfiles.add(creditProfile);
			return creditProfiles;
		}

		return creditProfiles;
	}

	public TelusCreditProfile getCreditProfile(RequestContext context, String creditProfileId) throws ExecutionException, InterruptedException {
		LOGGER.info("start getCreditProfile by credit profile id {}", creditProfileId);
		List<TelusCreditProfile> creditProfiles = creditProfileService.getCreditProfile(context, creditProfileId);

		TelusCreditProfile creditProfile = new TelusCreditProfile();
		if (CollectionUtils.isEmpty(creditProfiles)) {
			LOGGER.warn("CREDIT PROFILE NOT FOUND FOR CreditProfileId=" + creditProfileId);
			creditProfile.setId(creditProfileId);
			creditProfiles.add(creditProfile);
			return creditProfile;
		}

		if (creditProfiles.size() > 1) {
			for (TelusCreditProfile cp : creditProfiles) {
				if (cp.getId().equals(creditProfileId)) {
					return cp;
				}
			}
		}
		return creditProfiles.get(0);
	}

	private Map<String, MultiValueMap<String, String>> getQueryParameters(String queryString) {
		
		Map<String, List<String>> orMap = Arrays.stream(queryString.split("&"))
		        .filter(param -> param.contains(";"))
		        .flatMap(param -> Arrays.stream(param.split(";")))
		        .collect(Collectors.toMap(
		                orParam -> {
		                    String[] splitParam = orParam.split("=");
		                    return splitParam.length > 0 ? splitParam[0] : ""; // Check array length before accessing index 0
		                },
		                orParam -> {
		                    String[] splitParam = orParam.split("=");
		                    if (splitParam.length > 1) {
		                        return Arrays.stream(splitParam[1].split(","))
		                                .collect(Collectors.toList());
		                    } else {
		                        return Collections.emptyList();
		                    }
		                }
		        ));

		
		Map<String, List<String>> andMap = Arrays.stream(queryString.split("&"))
		        .filter(param -> Objects.nonNull(param) && !param.contains(";") && param.split("=").length == 2)
		        .collect(Collectors.toMap(
		                orParam -> {
		                    String[] splitParam = orParam.split("=");
		                    return splitParam.length > 0 ? splitParam[0] : ""; // Check array length before accessing index 0
		                },
		                orParam -> {
		                    String[] splitParam = orParam.split("=");
		                    if (splitParam.length > 1) {
		                        return Arrays.stream(splitParam[1].split(","))
		                                .collect(Collectors.toList());
		                    } else {
		                        return Collections.emptyList();
		                    }
		                }
		        ));
		
		
		Map<String, MultiValueMap<String, String>> operatorMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(orMap)) {
			operatorMap.put("OR", new LinkedMultiValueMap<>(orMap));
		}
		if (!CollectionUtils.isEmpty(andMap)) {
			operatorMap.put("AND", new LinkedMultiValueMap<>(andMap));
		}
		return operatorMap;
	}

}
