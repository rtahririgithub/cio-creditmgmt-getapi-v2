package com.telus.credit.firestore;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.telus.credit.exceptions.ExceptionConstants;
import com.telus.credit.exceptions.ExceptionHelper;
import com.telus.credit.exceptions.ReadStoreGenericException;
import com.telus.credit.firestore.model.CustomerDocument;
import com.telus.credit.model.Customer;
import com.telus.credit.model.common.MetaDataType;

@Service
public class CustomerCollectionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerCollectionService.class);

	@Autowired
	private Firestore firestore;

	@Autowired
	private SearchCriteriaBuilder queryBuilder;

	@Value("${firestore.collection.prefix}")
	private String collectionPrefix;

	private static final String NAME = "customers";

	public String addorUpdateCustomerCollection(Customer newCustomer) throws ReadStoreGenericException {
		
		String custID = (newCustomer!=null)?newCustomer.getId():null;
		LOGGER.debug("custID={} . addCustomerCollection input::{}", custID , newCustomer);
		CustomerDocument customerData = null;
		try {
			customerData = new CustomerDocument();
			customerData.setMetaData(populateMetaData(newCustomer));
			customerData.setCustomer(newCustomer);
			String id = getCurrentDocumentId(newCustomer.getId());
			LOGGER.debug("collectionData to be persisted::{}", customerData);
			if (id != null) {
				updateFireStoreDocument(customerData, id);
				LOGGER.info("Update existing Cutsomer Document id:{}", id);
				return id;
			} else {
				String docId = addFireStoreDocument(customerData);
				LOGGER.info("New Document added to Cutsomer collection:{}", docId);
				return docId;
			}
		} catch (InterruptedException | ExecutionException e) {
	        LOGGER.error("{}:Write to ReadDB failed for custId={} . customerData:{}  :{}", ExceptionConstants.STACKDRIVER_METRIC , custID, customerData, ExceptionHelper.getStackTrace(e));

			throw new ReadStoreGenericException(e);
		}

	}

	protected String getCollectionName() {
		return collectionPrefix + NAME;
	}

	private Map<String, Object> populateMetaData(Customer customer) {
		Map<String, Object> metaData = new HashMap<>();
		metaData.put(MetaDataType.CUSTOMER_ID.name(), customer.getId());

		LOGGER.debug("metaData generated::{}", metaData);
		return metaData;
	}
	protected String getCurrentDocumentId(String id) throws InterruptedException, ExecutionException {
		QuerySnapshot qSanpshot = firestore.collection(getCollectionName()).select(new String[] {})
				.whereEqualTo("metadata." + MetaDataType.CUSTOMER_ID.name(), id).get().get();
		if (!qSanpshot.isEmpty()) {
			List<String> docs = qSanpshot.getDocuments().stream().map(document -> {
				LOGGER.debug("Found Document id:{} for given customer id:{}", document.getId(), id);
				return document.getId();
			}).collect(Collectors.toList());

			if (!CollectionUtils.isEmpty(docs)) {
				return docs.get(0);
			}
		}
		return null;
	}

	protected void updateFireStoreDocument(CustomerDocument customerData, String id) {
		firestore.collection(getCollectionName()).document(id).set(customerData);
	}

	protected String addFireStoreDocument(CustomerDocument customerData)
			throws InterruptedException, ExecutionException {
		DocumentReference ref = firestore.collection(getCollectionName()).add(customerData).get();
		return ref.getId();
	}

	private Optional<Customer> getDocumentById(String id) throws InterruptedException, ExecutionException {
		QuerySnapshot qSanpshot = firestore.collection(getCollectionName()).whereEqualTo("metadata." + MetaDataType.CUSTOMER_ID.name(), id)
				.get().get();
		if (!qSanpshot.isEmpty()) {
			List<CustomerDocument> docs = qSanpshot.getDocuments().stream().map(document -> {
				LOGGER.debug("Found Document id:{} for given customer id:{}", document.getId(), id);
				CustomerDocument storeDoc = document.toObject(CustomerDocument.class);
				storeDoc.setFirestoreId(document.getId());
				storeDoc.setLastUpdateTimeInNanos(Instant.ofEpochSecond(document.getUpdateTime().getSeconds() , document.getUpdateTime().getNanos()).toEpochMilli());
				return storeDoc;
			}).collect(Collectors.toList());

			if (!CollectionUtils.isEmpty(docs)) {
				if (docs.size() > 1) {
					LOGGER.info("Found Duplicate Documents for customer id:{} ", id);
					boolean anyPublishTimeExist = docs.stream().anyMatch( storeDoc -> ObjectUtils.isNotEmpty(storeDoc.getPublishTimeinNanos()));
					if(anyPublishTimeExist) {
						LOGGER.info("Found Publish Time for the Documents for customer id:{} so sorting based on publishTime ", id);
						docs.sort(Comparator.comparing(CustomerDocument::getPublishTimeinNanos, Comparator.nullsFirst(Comparator.naturalOrder())));
					} else {
						LOGGER.info("No Publish Time for the Documents for customer id:{} so sorting based on updatedTime ", id);
						docs.sort(Comparator.comparing(CustomerDocument::getLastUpdateTimeInNanos));
					}
					LOGGER.info("Sorted Duplicate Documents for customer id:{} ", docs);
					/* As discussed in the meeting, disabling the code that deletes the doc from firestore
					 * for (int i = 0; i < (docs.size() - 1); i++) {
						LOGGER.info("Document to be deleted customer id:{} firestoreId:{}", id,
								docs.get(i).getFirestoreId());
						try {
							firestore.collection(getCollectionName()).document(docs.get(i).getFirestoreId()).delete();
						} catch (Exception e) {
							LOGGER.debug("Error deleting the document in firestoe for customer id:{}", id);
						}

					}*/
					return Optional.ofNullable(docs.get(docs.size() - 1).getCustomer());
				}
				return Optional.ofNullable(docs.get(0).getCustomer());
			}
		}
		return Optional.ofNullable(null);
	}

	public Optional<Customer> findDocument(String customerId) {
		try {
			return getDocumentById(customerId);
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("{}: query ReadDB failed for customerId:{} . {}", ExceptionConstants.STACKDRIVER_METRIC ,customerId,  ExceptionHelper.getStackTrace(e));
			throw ExceptionHelper
					.createGenericCreditException("Error querying Cutomer Document in readDB, error:" + e.getMessage());
		}
	}

	public Optional<Customer> findCreditProfileDocument(String creditProfileId) throws InterruptedException, ExecutionException {
		QuerySnapshot querySnapshot = firestore.collection(getCollectionName()).whereArrayContains("metadata." + MetaDataType.CREDIT_PROFILE_ID_LIST.name(), creditProfileId)
				.get().get();
		if (!querySnapshot.isEmpty()) {
			List<CustomerDocument> docs = querySnapshot.getDocuments().stream().map(document -> {
				CustomerDocument storeDoc = document.toObject(CustomerDocument.class);
				storeDoc.setFirestoreId(document.getId());
				storeDoc.setLastUpdateTimeInNanos(Instant.ofEpochSecond(document.getUpdateTime().getSeconds(), document.getUpdateTime().getNanos()).toEpochMilli());
				return storeDoc;
			}).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(docs)) {
				if (docs.size() > 1) {
					LOGGER.info("Found Duplicate Documents for credit profile id:{} ", creditProfileId);
					boolean anyPublishTimeExist = docs.stream().anyMatch( storeDoc -> ObjectUtils.isNotEmpty(storeDoc.getPublishTimeinNanos()));
					if(anyPublishTimeExist) {
						LOGGER.info("Found Publish Time for the Documents for credit profile id:{} so sorting based on publishTime ", creditProfileId);
						docs.sort(Comparator.comparing(CustomerDocument::getPublishTimeinNanos, Comparator.nullsFirst(Comparator.naturalOrder())));
					} else {
						LOGGER.info("No Publish Time for the Documents for credit profile id:{} so sorting based on updatedTime ", creditProfileId);
						docs.sort(Comparator.comparing(CustomerDocument::getLastUpdateTimeInNanos));
					}
					LOGGER.info("Sorted Duplicate Documents for credit profile id:{} ", docs);
					return Optional.ofNullable(docs.get(docs.size() - 1).getCustomer());
				}
				return Optional.ofNullable(docs.get(0).getCustomer());
			}
		}
		return Optional.ofNullable(null);
	}

	public Optional<List<Customer>> searchDocument(MultiValueMap<String, String> searchParams, boolean encoded) {
		try {
			List<Customer> results = new ArrayList<>();
			QuerySnapshot searchQueryRes = queryBuilder.buildQuery(searchParams,firestore.collection(getCollectionName()), encoded).get().get();
			Set<String> interalRef = new HashSet<>(); 
			if (!searchQueryRes.isEmpty()) {
				for(QueryDocumentSnapshot doc: searchQueryRes.getDocuments()) {
							if(!interalRef.contains(doc.getId())) {
								CustomerDocument storeDoc = doc.toObject(CustomerDocument.class);
								LOGGER.info("Found Doc id:{} customer id:{}", doc.getId(), storeDoc.getCustomer().getId());
								results.add(storeDoc.getCustomer());
								interalRef.add( doc.getId());
							}
						}
					}
				LOGGER.debug("Total Result size:{}",results.size());
				return !CollectionUtils.isEmpty(results)? Optional.of(results) : Optional.empty();
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("{}: query searchDocument failed error:{}", ExceptionConstants.STACKDRIVER_METRIC , ExceptionHelper.getStackTrace(e));
			throw ExceptionHelper
					.createGenericCreditException("Error querying Customer Document in readDB/firestore, error:" + e.getMessage());
		}
	}
	
}
