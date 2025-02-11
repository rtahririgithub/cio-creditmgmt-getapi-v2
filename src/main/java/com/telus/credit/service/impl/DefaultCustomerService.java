package com.telus.credit.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.telus.credit.common.CommonHelper;
import com.telus.credit.exceptions.ExceptionHelper;
import com.telus.credit.exceptions.ReadStoreGenericException;
import com.telus.credit.firestore.AssessmentCollectionService;
import com.telus.credit.firestore.CustomerCollectionService;
import com.telus.credit.firestore.model.AssesmentDocumentCompact;
import com.telus.credit.model.Customer;
import com.telus.credit.model.RiskLevelRiskAssessment;
import com.telus.credit.model.TelusCreditProfile;
import com.telus.credit.service.CustomerService;

@Service
public class DefaultCustomerService implements CustomerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCustomerService.class);

	private CustomerCollectionService readDB;

	private AssessmentCollectionService assesmentDB;

	private ResponseInterceptorService responseInterceptorService;

	public DefaultCustomerService(CustomerCollectionService readDB,
								  AssessmentCollectionService assesmentDB,
								   ResponseInterceptorService responseInterceptorService) {
		this.readDB = readDB;
		this.assesmentDB = assesmentDB;
		this.responseInterceptorService = responseInterceptorService;
	}


	@Override
	public Optional<Customer> findCustomerFromStore(String custId, String lang) {
		Optional<Customer> optCust = Optional.ofNullable(null);
		Optional<Customer> optionalData = readDB.findDocument(custId);
		if (optionalData.isPresent()) {
			Customer customer = optionalData.get();
			getCurrentAssesmentCodes(customer);
			enrichData(customer, lang);
			optCust = Optional.of(customer);
		}
		return optCust;
	}

	@Override
	public Customer getCurrentAssesmentCodes(Customer customer) {
		Optional<Map<String, AssesmentDocumentCompact>> assesmentDocumentCompactMap = Optional.empty();
		try {
			assesmentDocumentCompactMap = assesmentDB.getCurrentAssesmentCode(
						Arrays.asList(new String[] { customer.getId() })
						);
		} catch (ReadStoreGenericException e) {
			LOGGER.warn("Ignoring Assement code lookup error from ReadDB", ExceptionHelper.getStackTrace(e));			
		}
		if (assesmentDocumentCompactMap.isPresent() && !assesmentDocumentCompactMap.get().isEmpty()) {
			AssesmentDocumentCompact aAssesmentDocumentCompact = assesmentDocumentCompactMap.get().get(customer.getId());
			final String code = (aAssesmentDocumentCompact !=null)?aAssesmentDocumentCompact.getAssessmentMessageCd():"";
			final String creditAssessmentTypeCd= (aAssesmentDocumentCompact !=null)?aAssesmentDocumentCompact.getCreditAssessmentTypeCd():"";
			final String creditAssessmentSubTypeCd=(aAssesmentDocumentCompact !=null)? aAssesmentDocumentCompact.getCreditAssessmentSubTypeCd():"";
			
			if (ObjectUtils.isNotEmpty(customer.getCreditProfile().get(0).getTelusCharacteristic())) {
				List<TelusCreditProfile> profiles = CommonHelper.nullSafe(customer.getCreditProfile()).stream()
						.map(profile -> {
							profile.getTelusCharacteristic().setAssessmentMessageCode(code);
							return profile;
						}).collect(Collectors.toList());
				customer.setCreditProfile(profiles);
			} else {
				List<TelusCreditProfile> profiles = 
						CommonHelper.nullSafe(customer.getCreditProfile())
						.stream()
						.map(profile -> {
							if( profile.getLastRiskAssessment()==null) {
								profile.setLastRiskAssessment(new RiskLevelRiskAssessment());
							}
							profile.getLastRiskAssessment().setAssessmentMessageCd(code);
							profile.getLastRiskAssessment().setCreditAssessmentTypeCd(creditAssessmentTypeCd);
							profile.getLastRiskAssessment().setCreditAssessmentSubTypeCd(creditAssessmentSubTypeCd);
							return profile;
						})
						.collect(Collectors.toList());
				
				customer.setCreditProfile(profiles);
			}
		}
		return customer;
	}

	private void enrichData(Customer customer, String lang) {
		responseInterceptorService.resolveMissingFields(customer, lang);
	}

}
