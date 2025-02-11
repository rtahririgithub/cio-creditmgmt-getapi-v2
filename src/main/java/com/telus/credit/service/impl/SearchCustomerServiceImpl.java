package com.telus.credit.service.impl;

import com.google.common.collect.Lists;
import com.telus.credit.common.CommonHelper;
import com.telus.credit.common.DateTimeUtils;
import com.telus.credit.exceptions.ExceptionHelper;
import com.telus.credit.firestore.AssessmentCollectionService;
import com.telus.credit.firestore.CustomerCollectionService;
import com.telus.credit.firestore.model.AssesmentDocumentCompact;
import com.telus.credit.model.Customer;
import com.telus.credit.model.RelatedParty;
import com.telus.credit.model.RiskLevelRiskAssessment;
import com.telus.credit.model.TelusCreditProfile;
import com.telus.credit.model.TelusIndividualIdentification;
import com.telus.credit.model.common.ApplicationConstants;
import com.telus.credit.service.SearchCustomerService;
import com.telus.credit.utils.CreditProfileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SearchCustomerServiceImpl implements SearchCustomerService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchCustomerServiceImpl.class);

	@Autowired
	private CustomerCollectionService readDB;
	
	@Autowired
	private AssessmentCollectionService assesmentDB;
	
	@Autowired
	private ResponseInterceptorService customerEnrichmentService;
	private List<Customer> getCurrentAssesmentCodes2(List<Customer> customers) {
		for (Customer customer : customers) {					
			List<TelusCreditProfile> creditProfileList = customer.getCreditProfile();
			for (TelusCreditProfile telusCreditProfile : creditProfileList) {
				
				List<TelusCreditProfile> cpList =new ArrayList<TelusCreditProfile>();
				cpList.add(telusCreditProfile);
				Customer acustomer = new Customer();
				acustomer.setCreditProfile(cpList);
				List<Customer> custs = new ArrayList<Customer>();
				custs.add(acustomer);
				
				List<Customer> custsWithassessmentCodes = getCurrentAssesmentCodes(custs);
			}
			// for customer.getCreditprofile 
			//call getCurrentAssesmentCodes(List<Customer> customers) with only one of the creditprofile 
			//merge the result.
		}
		return customers;
		
	}
	private List<Customer> getCurrentAssesmentCodes(List<Customer> customers) {
		LOGGER.info("Start getCurrentAssesmentCodes");
		if( customers==null || customers.isEmpty()) {
			return customers;
		}		
		List<TelusCreditProfile> creditProfiles= customers.get(0).getCreditProfile();
		if(creditProfiles==null || creditProfiles.isEmpty()) {
			return customers;
		}
		
		Optional<Map<String, AssesmentDocumentCompact>> assementCodesMap = Optional.empty();
		try {
			
			List<String> customerIds = CommonHelper.nullSafe(customers)
					.stream().filter(custObj -> StringUtils.isNotEmpty(custObj.getId()))
					.map(Customer::getId).collect(Collectors.toList());
			
			assementCodesMap = assesmentDB.getCurrentAssesmentCode(customerIds);
		} catch (Throwable e) {
			LOGGER.warn("Ignoring Assement code lookup error from ReadDB", ExceptionHelper.getStackTrace(e));
		}
		if (assementCodesMap.isPresent() ) {
			if( !assementCodesMap.get().isEmpty()) {
				Map<String, AssesmentDocumentCompact> codesMap = assementCodesMap.get();
				for (Customer customer : CommonHelper.nullSafe(customers)) {
					if (ObjectUtils.isNotEmpty(customer.getCreditProfile().get(0).getTelusCharacteristic())) {
						for (TelusCreditProfile profile : CommonHelper.nullSafe(customer.getCreditProfile())) {
							String lob = profile.getLineOfBusiness();
							AssesmentDocumentCompact aAssesmentDocumentCompact1 = codesMap.get(customer.getId()+lob);
							String code1 = (aAssesmentDocumentCompact1 !=null)?aAssesmentDocumentCompact1.getAssessmentMessageCd():"";
							profile.getTelusCharacteristic().setAssessmentMessageCode(code1);
						}
					}
					else {
						for (TelusCreditProfile profile : CommonHelper.nullSafe(customer.getCreditProfile())) {
							if(profile.getLastRiskAssessment()==null ) {
								profile.setLastRiskAssessment(new RiskLevelRiskAssessment());
							}
							String lob = profile.getLineOfBusiness();
							AssesmentDocumentCompact aAssesmentDocumentCompact1 = codesMap.get(customer.getId()+lob);
							if(aAssesmentDocumentCompact1!=null) {
								if(aAssesmentDocumentCompact1!=null) {
									String code1 = aAssesmentDocumentCompact1.getAssessmentMessageCd();
									String creditAssessmentTypeCd= aAssesmentDocumentCompact1.getCreditAssessmentTypeCd();
									String creditAssessmentSubTypeCd= aAssesmentDocumentCompact1.getCreditAssessmentSubTypeCd();							
									profile.getLastRiskAssessment().setAssessmentMessageCd(code1);
									profile.getLastRiskAssessment().setCreditAssessmentTypeCd(creditAssessmentTypeCd);
									profile.getLastRiskAssessment().setCreditAssessmentSubTypeCd(creditAssessmentSubTypeCd);
								}
							}
							
							
						}
					}
				}
			}
		}
		return customers;
	}

	@Override
	public Optional<List<Customer>> searchCustomerInStore(Map<String, MultiValueMap<String,String>> searchParamsMap, String lang) {
		Set<String> sortVals = null;
		Set<String> filters = null;
		boolean decending = false;

		LOGGER.debug("searchCustomerInStore searchParams:{}", searchParamsMap);
		MultiValueMap<String, String> searchParams = new LinkedMultiValueMap<>();
		if(searchParamsMap.containsKey("AND")) {
			searchParams.addAll(searchParamsMap.get("AND"));
		}
		if(searchParamsMap.containsKey("OR")) {
			searchParams.addAll(searchParamsMap.get("OR"));
		}
		//sorting
		if (searchParams.containsKey(ApplicationConstants.SORT_KEY)) {
			sortVals = Stream.of(StringUtils.split(searchParams.get(ApplicationConstants.SORT_KEY).get(0), ","))
					.map(String::trim)
					.filter(val -> StringUtils.isNotBlank(val)
							&& (StringUtils.containsIgnoreCase(val, ApplicationConstants.SORT_BY_RISK_RATING)
							|| StringUtils.containsIgnoreCase(val, ApplicationConstants.SORT_BY_START_DTM)))
					.map(val -> RegExUtils.replaceAll(val, "[+-]", ""))
					.collect(Collectors.toSet());
			decending = StringUtils.containsIgnoreCase(searchParams.get(ApplicationConstants.SORT_KEY).get(0), "-");
			searchParams.remove(ApplicationConstants.SORT_KEY);
		}

		//filtering
		if (searchParams.containsKey(ApplicationConstants.FILTER_KEY)) {
			filters = Stream.of(StringUtils.split(searchParams.get(ApplicationConstants.FILTER_KEY).get(0), ","))
					.map(String::trim)
					.filter(val -> StringUtils.isNotBlank(val)
							&& (StringUtils.containsIgnoreCase(val, ApplicationConstants.FILTER_BY_NONE)
							|| StringUtils.containsIgnoreCase(val, ApplicationConstants.FILTER_BY_PROFILE)))
					.collect(Collectors.toSet());
			searchParams.remove(ApplicationConstants.FILTER_KEY);
		}
		
		//get key MATCHEDBYCARDID_KEY from request paramsget matching customers  
		boolean matchedByCardIdInd=false;
		if (searchParams.containsKey(ApplicationConstants.MATCHEDBYCARDID_KEY)) {
			String matchedByCardIValue = searchParams.get(ApplicationConstants.MATCHEDBYCARDID_KEY).get(0);
			matchedByCardIdInd=Boolean.parseBoolean(matchedByCardIValue); 
			//MATCHEDBYCARDID  is not part of metadata in firestore
			searchParams.remove(ApplicationConstants.MATCHEDBYCARDID_KEY);
			//MATCHEDBYCARDID_KEY can be used only in combination of  customerId .
			if ( searchParams.size()!=1 ) {
				matchedByCardIdInd=false;
			}			
		}	
		//get lineofbusiness from request
		final String searchParamLineOfBusiness=(searchParams.containsKey(ApplicationConstants.LOB_KEY))?searchParams.get(ApplicationConstants.LOB_KEY).get(0):"";
		if (searchParams.containsKey(ApplicationConstants.LOB_KEY)) {
			//LOB is not part of metadata in firestore
			searchParams.remove(ApplicationConstants.LOB_KEY);			
		}		
		
//perform search in firestore
		LOGGER.debug("searchCustomerInStore updated searchParams:{}", searchParams);
		final List<Customer> finalCustomerList = new ArrayList<>();
		//Note: FireStore has limited support for Logic OR operator, for OR queries we have to perform separate query for reach and then merge results.
		if (searchParamsMap.containsKey("OR")) {
			MultiValueMap<String, String> searchOrParams = searchParamsMap.get("OR");
			Optional<List<Customer>> customers = getCustomersWithMatchingIds(searchParamsMap.get("AND"), searchOrParams);
			if (customers.isPresent() && CollectionUtils.isNotEmpty(customers.get())) {
				finalCustomerList.addAll(customers.get());
			}
		} else {
			Optional<List<Customer>> resultCustomerOptional = readDB.searchDocument(searchParams, true);
			if (resultCustomerOptional.isPresent() && CollectionUtils.isNotEmpty(resultCustomerOptional.get())) {
				finalCustomerList.addAll(resultCustomerOptional.get());
			}
		}

		if (CollectionUtils.isNotEmpty(finalCustomerList)) {
			List<Customer> resultCustomerList;
			try {
			resultCustomerList = new ArrayList<>(finalCustomerList.stream().collect(Collectors.toMap(Customer::getId, customer -> customer)).values());
			}catch (java.lang.IllegalStateException e) {
				//in case there are duplicated customers. ( resultList contains multiple customers for the same customerId).
				resultCustomerList = new ArrayList<>(finalCustomerList.stream().collect(Collectors.toMap(Customer::getId, customer -> customer, (a1, a2) -> a1)).values());
			}
			
			finalCustomerList.removeIf(customer -> true);
			finalCustomerList.addAll(resultCustomerList);
		}
		List<String> customerIds = searchParams.get(ApplicationConstants.CUSTOMER_ID_KEY);
		if (matchedByCardIdInd && CollectionUtils.isNotEmpty(customerIds) && CollectionUtils.isNotEmpty(finalCustomerList)) {
			//get customer's Identification (cardIds)
			Map<String, String> idMap = finalCustomerList.stream()
					//.map(CreditProfileUtil::getPrimaryCreditProfile)
					.flatMap(customer -> customer.getCreditProfile().stream())
					.flatMap(creditProfile -> CreditProfileUtil.getRelatedPartiesWithCustomerRole(creditProfile).stream())
					.map(RelatedParty::getIndividual)
					.filter(Objects::nonNull)
					.flatMap(individual -> individual.getIndividualIdentification().stream())
					.filter(telusIndividualIdentification -> Objects.nonNull(telusIndividualIdentification) && Objects.nonNull(telusIndividualIdentification.getIdentificationIdHashed()) && Objects.nonNull(telusIndividualIdentification.getIdentificationType())
					)
					.collect(Collectors.toMap(TelusIndividualIdentification::getIdentificationType, TelusIndividualIdentification::getIdentificationIdHashed));
			String customerId = customerIds.get(0);
			
		//if (matchedByCardIdInd) 
		{
			//find and add other creditprofiles with matching Identification as the input customerId
			finalCustomerList.addAll(
				//search for matching customers by Identification(idMap)
					idMap.entrySet().stream().map(entry -> 
						{
							MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
							valueMap.put(entry.getKey(), Lists.newArrayList(entry.getValue()));
						//search by Identification
							return readDB.searchDocument(valueMap, false);
						}
					)
					.filter(Optional::isPresent)
					.flatMap(customers1 -> customers1.get().stream())
				//remove the current creditprofile from the result
					.filter(customer -> !customer.getId().equals(customerId))
				//add the other creditprofiles as secondary creditprofile
					.peek(customer -> {
						List<TelusCreditProfile> creditProfiles = customer.getCreditProfile();
						if (CollectionUtils.isNotEmpty(creditProfiles)) {
							creditProfiles.forEach(telusCreditProfile -> telusCreditProfile.setCustomerCreditProfileRelCd(ApplicationConstants.SECONDARY));
						}
					})
					.collect(Collectors.toList())
					);
		}
		}
		LOGGER.debug("searchCustomerInStore results:{}", finalCustomerList);
		Optional<List<Customer>> finalCustomerListOptional = Optional.empty();
		if (CollectionUtils.isNotEmpty(finalCustomerList)) {
			
			//filter by earchParam lineOfbuiness
			final String searchParamLineOfBusiness2=(
						searchParamLineOfBusiness.equalsIgnoreCase("WIRELINE") || searchParamLineOfBusiness.equalsIgnoreCase("WIRELESS")
						)?searchParamLineOfBusiness:"";					
			if (searchParamLineOfBusiness2!=null && !searchParamLineOfBusiness2.trim().isEmpty()) {
				List<TelusCreditProfile> filteredEntities = finalCustomerList.get(0).getCreditProfile().stream()
				        .filter(entity -> entity.getLineOfBusiness().equals(searchParamLineOfBusiness2))
				        .collect(Collectors.toList());
				finalCustomerList.get(0).setCreditProfile(filteredEntities);
			}
			
			
			
			LOGGER.info("Credit Profile Size {}", finalCustomerList.get(0).getCreditProfile());
			finalCustomerListOptional = handleSearchResults(finalCustomerList, lang, sortVals, decending, filters);
		}
		return finalCustomerListOptional;
	}

	private Optional<List<Customer>> getCustomersWithMatchingIds(MultiValueMap<String, String> searchAndParams, MultiValueMap<String, String> searchOrParams) {
		List<Customer> resultList = new LinkedList<>();
		searchOrParams.entrySet().forEach(searchOrParam -> {
			MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
			if(Objects.nonNull(searchAndParams)){
				valueMap.addAll(searchAndParams);
			}
			valueMap.put(searchOrParam.getKey(), searchOrParam.getValue());
			Optional<List<Customer>> resultCustomerOptional = readDB.searchDocument(valueMap, true);
			if (resultCustomerOptional.isPresent() && CollectionUtils.isNotEmpty(resultCustomerOptional.get())) {
				resultList.addAll(resultCustomerOptional.get());
			}
			valueMap.remove(searchOrParam);
		});
		return Optional.of(resultList);
	}

	private Optional<List<Customer>> handleSearchResults(List<Customer> finalCustomerList, String lang, Set<String> sortVals, boolean decending, Set<String> filters) {
		Optional<List<Customer>> optWrappedRes;
		List<Customer> resultDecorated = new ArrayList<>(finalCustomerList.size());
		
		List<Customer> finalCustomerListWithassessmentCode = getCurrentAssesmentCodes(finalCustomerList);
		
		for (Customer obj : CommonHelper.nullSafe(finalCustomerListWithassessmentCode)) {
			customerEnrichmentService.resolveMissingFields(obj, lang);
			if (CollectionUtils.isNotEmpty(sortVals)) {
				if(sortVals.contains(ApplicationConstants.SORT_BY_RISK_RATING)) {
					handelRatingRanking(obj, decending);
				}
				if (sortVals.contains(ApplicationConstants.SORT_BY_START_DTM)) {
					handelValidForDateRanking(obj, decending);
				}
			}
			resultDecorated.add(obj);
		}
		if (CollectionUtils.isNotEmpty(resultDecorated) && resultDecorated.size() > 1 && CollectionUtils.isNotEmpty(sortVals)) {
			optWrappedRes = Optional.of(handleSorting(sortVals, resultDecorated, decending));
		} else {
			optWrappedRes = Optional.of(resultDecorated);
		}
		if(CollectionUtils.isNotEmpty(filters) && optWrappedRes.isPresent() ) {
				optWrappedRes = Optional.of(filterData(optWrappedRes.get(), filters));
		}
		return optWrappedRes;
	}

	private List<Customer> filterData(List<Customer> customers, Set<String> filters) {
		if(filters.contains(ApplicationConstants.FILTER_BY_NONE)) {
			return CommonHelper.nullSafe(customers).stream().map(cust -> {
				 cust.setCreditProfile(null);
				 return cust;
			 }).collect(Collectors.toCollection(LinkedList::new));
			
		}
		if(filters.containsAll(Arrays.asList(ApplicationConstants.FILTER_BY_PROFILE))) {
			return customers;
		}
//		if(filters.contains(ApplicationConstants.FILTER_BY_PARTY)) {
//			return CommonHelper.nullSafe(customers).stream().map(cust -> {
//				 cust.setCreditProfile(null);
//				 return cust;
//			 }).collect(Collectors.toCollection(LinkedList::new));
//		}
		if(filters.contains(ApplicationConstants.FILTER_BY_PROFILE)) {
				return CommonHelper.nullSafe(customers).stream().map(cust -> {
					 return cust;
				 }).collect(Collectors.toCollection(LinkedList::new));
			}
		return customers;
	}
	
	private List<Customer> handleSorting(Set<String> sortVals, List<Customer> resultDecorated, boolean decending) {
		if (sortVals.contains(ApplicationConstants.SORT_BY_RISK_RATING)) {
			Comparator<Customer> comparator = Comparator.comparing(Customer::getSortRankingRatingVal);
			if (!decending) {
				resultDecorated.sort(comparator);
			} else {
				resultDecorated.sort(comparator.reversed());
			}

		}
		if (sortVals.contains(ApplicationConstants.SORT_BY_START_DTM)) {
			Comparator<Customer> comparator = Comparator.comparing(Customer::getSortRankingValidForVal);
			if (!decending) {
				resultDecorated.sort(comparator);
			} else {
				resultDecorated.sort(comparator.reversed());
			}

		}
		return resultDecorated;
	}

	private void handelRatingRanking(Customer obj, boolean decending) {
		if (!decending) {
			Optional<TelusCreditProfile> min = CommonHelper.nullSafe(obj.getCreditProfile()).stream()
					.min((profile1, profile2) -> Integer.compare(
							NumberUtils.toInt(profile1.getCreditRiskLevelNum(), Integer.MAX_VALUE),
							NumberUtils.toInt(profile2.getCreditRiskLevelNum(), Integer.MAX_VALUE)));
			if (min.isPresent()) {
				obj.setSortRankingRatingVal(NumberUtils.toInt(min.get().getCreditRiskLevelNum(), Integer.MAX_VALUE));
			}
		} else {
			Optional<TelusCreditProfile> max = CommonHelper.nullSafe(obj.getCreditProfile()).stream()
					.max((profile1, profile2) -> Integer.compare(
							NumberUtils.toInt(profile1.getCreditRiskLevelNum(), Integer.MIN_VALUE),
							NumberUtils.toInt(profile2.getCreditRiskLevelNum(), Integer.MIN_VALUE)));
			if (max.isPresent()) {
				obj.setSortRankingRatingVal(NumberUtils.toInt(max.get().getCreditRiskLevelNum(), Integer.MAX_VALUE));
			}
		}
	}

	private void handelValidForDateRanking(Customer obj, boolean decending) {
		if (!decending) {
			handelMinValidFor(obj);
		} else {
			handelMaxValidFor(obj);
		}
	}
	
	private void handelMinValidFor(Customer obj) {
		Optional<TelusCreditProfile> min = CommonHelper.nullSafe(obj.getCreditProfile()).stream()
				.min((profile1, profile2) -> {
					String date1 = (ObjectUtils.isNotEmpty(profile1.getValidFor())
							&& StringUtils.isNotBlank(profile1.getValidFor().getStartDateTime()))
									? profile1.getValidFor().getStartDateTime()
									: ApplicationConstants.DEFAULT_DT_TM;
					String date2 = (ObjectUtils.isNotEmpty(profile2.getValidFor())
							&& StringUtils.isNotBlank(profile2.getValidFor().getStartDateTime()))
									? profile2.getValidFor().getStartDateTime()
									: ApplicationConstants.DEFAULT_DT_TM;
					return DateTimeUtils.toUtcTimestamp(date1).compareTo(DateTimeUtils.toUtcTimestamp(date2));
				});
		if (min.isPresent()) {
			Timestamp stDtTm = (ObjectUtils.isNotEmpty(min.get().getValidFor())
					&& StringUtils.isNotBlank(min.get().getValidFor().getStartDateTime()))
							? DateTimeUtils.toUtcTimestamp(min.get().getValidFor().getStartDateTime())
							: DateTimeUtils.toUtcTimestamp(ApplicationConstants.DEFAULT_DT_TM);
			obj.setSortRankingValidForVal(stDtTm);
		}
	}
	
	private void handelMaxValidFor(Customer obj) {
		Optional<TelusCreditProfile> max = CommonHelper.nullSafe(obj.getCreditProfile()).stream()
				.max((profile1, profile2) -> {
					String date1 = (ObjectUtils.isNotEmpty(profile1.getValidFor())
							&& StringUtils.isNotBlank(profile1.getValidFor().getStartDateTime()))
									? profile1.getValidFor().getStartDateTime()
									: ApplicationConstants.DEFAULT_DT_TM;
					String date2 = (ObjectUtils.isNotEmpty(profile2.getValidFor())
							&& StringUtils.isNotBlank(profile2.getValidFor().getStartDateTime()))
									? profile2.getValidFor().getStartDateTime()
									: ApplicationConstants.DEFAULT_DT_TM;
					return DateTimeUtils.toUtcTimestamp(date1).compareTo(DateTimeUtils.toUtcTimestamp(date2));
				});
		if (max.isPresent()) {
			Timestamp stDtTm = (ObjectUtils.isNotEmpty(max.get().getValidFor())
					&& StringUtils.isNotBlank(max.get().getValidFor().getStartDateTime()))
							? DateTimeUtils.toUtcTimestamp(max.get().getValidFor().getStartDateTime())
							: DateTimeUtils.toUtcTimestamp(ApplicationConstants.DEFAULT_DT_TM);
			obj.setSortRankingValidForVal(stDtTm);
		}
	}
}
