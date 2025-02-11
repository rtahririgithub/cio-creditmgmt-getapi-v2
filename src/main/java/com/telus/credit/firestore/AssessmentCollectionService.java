package com.telus.credit.firestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.common.collect.Lists;
import com.telus.credit.exceptions.ExceptionConstants;
import com.telus.credit.exceptions.ExceptionHelper;
import com.telus.credit.exceptions.ReadStoreGenericException;
import com.telus.credit.firestore.model.AssesmentDocumentCompact;
import com.telus.credit.model.Customer;

@Service
public class AssessmentCollectionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AssessmentCollectionService.class);

	@Autowired
	private Firestore firestore;

	@Value("${assesment.collection.name}")
	private String collectionName;

	private static final String ASSESSMENT_COL = "assessmentMessageCd";
	private static final String FIELD = "customerId";
	private static final int BATCH_SIZE = 10;

	public Optional<Map<String, AssesmentDocumentCompact>> getCurrentAssesmentCode(List<String> customerIds) throws ReadStoreGenericException {
		LOGGER.debug("getCurrentAssesmentCode customerId::{}", customerIds);
		Optional<Map<String, AssesmentDocumentCompact>> results = Optional.empty();
		if (!CollectionUtils.isEmpty(customerIds)) {
			try {
				if (customerIds.size() > 10) {
					results = getBatchedResults(customerIds);
				} else {
					results = getAssmentDocuments(customerIds);
				}
			} catch (InterruptedException | ExecutionException e) {
				LOGGER.error("{}: Assessment Query to ReadDB failed for customerId:{}. {}", ExceptionConstants.STACKDRIVER_METRIC ,customerIds,  ExceptionHelper.getStackTrace(e));
				throw new ReadStoreGenericException(e);
			}
		}
		return results;
	}

	private Optional<Map<String, String>> getAssmentDocuments1(List<String> customerIds) throws InterruptedException, ExecutionException {
		Optional<Map<String, String>> results = Optional.empty();
		//WCMMSA-542:Fix for NullPointerException
		try {		
			QuerySnapshot qSanpshot = firestore.collection(collectionName).select(ASSESSMENT_COL, FIELD).whereIn(FIELD, customerIds).get().get();			
			//if (!qSanpshot.isEmpty() && qSanpshot.getDocuments()!=null) 
			{
				Stream<AssesmentDocumentCompact> assesmentDocumentCompactStream = qSanpshot.getDocuments()
																		.stream()
																		.map(doc -> 
																				{
																					LOGGER.info("Found Assessment Document id:{}", doc.getId());
																					return doc.toObject(AssesmentDocumentCompact.class);
																				}
																		    );
				Map<String, String> assesmentCodes = new HashMap<>();
				
				if(assesmentDocumentCompactStream!=null  ) {
					
					assesmentCodes=assesmentDocumentCompactStream
							.collect(
									Collectors.toMap(
										AssesmentDocumentCompact::getCustomerId,
										AssesmentDocumentCompact::getAssessmentMessageCd
										)
							);	
				}			
				results = Optional.of(assesmentCodes);		
			}
		} catch (Throwable e) {
			LOGGER.warn("getAssmentDocuments failed :{}",e.getMessage(),e);
		}		
		return results;
	}

	private Optional<Map<String, AssesmentDocumentCompact>> getAssmentDocumentsorig(List<String> customerIds) throws InterruptedException, ExecutionException {
		Optional<Map<String, AssesmentDocumentCompact>> results = Optional.empty();
		try {		
//			QuerySnapshot qSanpshot = firestore.collection(collectionName).select(ASSESSMENT_COL, FIELD).whereIn(FIELD, customerIds).get().get();		
			QuerySnapshot qSanpshot = firestore.collection(collectionName).whereIn(FIELD, customerIds).get().get();	
			{
				Stream<AssesmentDocumentCompact> assesmentDocumentCompactStream = qSanpshot.getDocuments()
																		.stream()
																		.map(doc -> 
																				{
																					LOGGER.info("Found Assessment Document id:{}", doc.getId());
																					return doc.toObject(AssesmentDocumentCompact.class);
																				}
																		    );
				Map<String, AssesmentDocumentCompact> assesmentDocumentMap= new HashMap<>();
				
				//in case there are duplicated customers. ( resultList contains multiple customers for the same customerId).
				if(assesmentDocumentCompactStream!=null  ) {
					assesmentDocumentMap=assesmentDocumentCompactStream
							.collect(
									Collectors.toMap(
										AssesmentDocumentCompact::getCustomerId,
										Function.identity(),
										(a1, a2) -> a1
										)
							);	
				}			
				results = Optional.of(assesmentDocumentMap);		
			}
		} catch (Throwable e) {
			LOGGER.warn("getAssmentDocuments failed :{}",e.getMessage(),e);
		}		
		return results;
	}
	
	private String getLineOfBusiness(AssesmentDocumentCompact aDoc) {
        String lob="";
		List<String> wlsCreditAssessmentSubTypeCdlist = new ArrayList<>(Arrays.asList(
        		"CREDIT_CHECK",
        		"MANUAL_ASSESSMENT",
                "CREDIT_RESULT_OVRD",
                "MONTHLY_CCUD",
                "ADDON",
                "ADDON_ESTIMATOR",
                "ADDON_RESUME"
        ));		
		String creditAssessmentSubTypeCd = aDoc.getCreditAssessmentSubTypeCd();
		if(creditAssessmentSubTypeCd!=null) {
			String upperCaseStr = creditAssessmentSubTypeCd.toUpperCase();
			if (wlsCreditAssessmentSubTypeCdlist.contains(upperCaseStr)) {
				lob= "WIRELESS";
			}else {
				lob=  "WIRELINE";
			}
		}
		return lob;
	}
	public Optional<Map<String, AssesmentDocumentCompact>> getAssmentDocuments(List<String> customerIds) throws InterruptedException, ExecutionException {
		Optional<Map<String, AssesmentDocumentCompact>> results = Optional.empty();
		try {		
			//get all AssmentDocuments by customerID ( may contain AssmentDocuments for both wln and wls  assessmentsubytyps
			QuerySnapshot qSanpshot = firestore.collection(collectionName).whereIn(FIELD, customerIds).get().get();	
			{
				Stream<AssesmentDocumentCompact> assesmentDocumentCompactStream = qSanpshot.getDocuments()
																		.stream()
																		.map(doc -> 
																				{
																					return doc.toObject(AssesmentDocumentCompact.class);
																				}
																		    );
				Map<String, AssesmentDocumentCompact> assesmentDocumentMap= new HashMap<>();				
				// Collect elements into an ArrayList
				List<AssesmentDocumentCompact> assesmentDocumentCompactList = assesmentDocumentCompactStream.collect(Collectors.toList());
				for (AssesmentDocumentCompact document : assesmentDocumentCompactList) {
						String lob =getLineOfBusiness(document);
						document.setLineOfBusiness(lob);
						assesmentDocumentMap.put(document.getCustomerId()+lob, document);
				}			
				results = Optional.of(assesmentDocumentMap);		
			}
		} catch (Throwable e) {
			LOGGER.warn("getAssmentDocuments failed :{}",e.getMessage(),e);
		}		
		return results;
	}	
	public List<AssesmentDocumentCompact> getAssmentDocumentByCustID(String customerId) throws InterruptedException, ExecutionException {
		List<AssesmentDocumentCompact> assesmentDocumentCompactList = new ArrayList<AssesmentDocumentCompact>();

		Optional<Map<String, AssesmentDocumentCompact>> results = Optional.empty();
		try {		
			//get all AssmentDocuments by customerID ( may contain AssmentDocuments for both wln and wls  assessmentsubytyps
			QuerySnapshot qSanpshot = firestore.collection(collectionName).whereEqualTo("customerId", customerId).get().get();	
			{
				Stream<AssesmentDocumentCompact> assesmentDocumentCompactStream = qSanpshot.getDocuments()
																		.stream()
																		.map(doc -> 
																				{
																					return doc.toObject(AssesmentDocumentCompact.class);
																				}
																		    );
				Map<String, AssesmentDocumentCompact> assesmentDocumentMap= new HashMap<>();				
				// Collect elements into an ArrayList
				assesmentDocumentCompactList = assesmentDocumentCompactStream.collect(Collectors.toList());					
			}
		} catch (Throwable e) {
			LOGGER.warn("getAssmentDocuments failed :{}",e.getMessage(),e);
		}		
		return assesmentDocumentCompactList;
	}	
	public Optional<Map<String, AssesmentDocumentCompact>> getAssmentDocumentsOrig(List<String> customerIds) throws InterruptedException, ExecutionException {
		Optional<Map<String, AssesmentDocumentCompact>> results = Optional.empty();
		try {		
//			QuerySnapshot qSanpshot = firestore.collection(collectionName).select(ASSESSMENT_COL, FIELD).whereIn(FIELD, customerIds).get().get();		
			QuerySnapshot qSanpshot = firestore.collection(collectionName).whereIn(FIELD, customerIds).get().get();	
			{
				Stream<AssesmentDocumentCompact> assesmentDocumentCompactStream = qSanpshot.getDocuments()
																		.stream()
																		.map(doc -> 
																				{
																					return doc.toObject(AssesmentDocumentCompact.class);
																				}
																		    );
				Map<String, AssesmentDocumentCompact> assesmentDocumentMap= new HashMap<>();
				
				//in case there are duplicated customers. ( resultList contains multiple customers for the same customerId).
				if(assesmentDocumentCompactStream!=null  ) {
					assesmentDocumentMap=assesmentDocumentCompactStream
							.collect(
									Collectors.toMap(
										AssesmentDocumentCompact::getCustomerId,
										Function.identity(),(a1, a2) -> a1
										)
							);	
				}			
				results = Optional.of(assesmentDocumentMap);		
			}
		} catch (Throwable e) {
			LOGGER.warn("getAssmentDocuments failed :{}",e.getMessage(),e);
		}		
		return results;
	}		
	private Optional<Map<String, AssesmentDocumentCompact>> getBatchedResults(List<String> customerIds) throws InterruptedException, ExecutionException {
		Optional<Map<String, AssesmentDocumentCompact>> results = Optional.empty();
		List<List<String>> batches = Lists.partition(customerIds, BATCH_SIZE);
		Map<String, AssesmentDocumentCompact> fullRes = new HashMap<>();
		for( List<String> ids : batches) {
			Optional<Map<String, AssesmentDocumentCompact>> batchRes = getAssmentDocuments(ids);
			if(batchRes.isPresent()) {
				fullRes.putAll(batchRes.get());
			}
		}
		if(!CollectionUtils.isEmpty(fullRes)) {
			results = Optional.of(fullRes);
		}
		return results;
	}
}
