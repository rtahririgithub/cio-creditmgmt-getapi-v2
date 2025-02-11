package com.telus.credit.firestore;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Query;
import com.telus.credit.crypto.service.HashService;
import com.telus.credit.model.common.ApplicationConstants;
import com.telus.credit.model.common.MetaDataType;
import com.telus.credit.model.common.SearchableParams;

@Component
public class SearchCriteriaBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchCriteriaBuilder.class);

	@Value("${firestore.collection.limit:20}")
	private int limit;
	
	@Autowired
	HashService hash;

	private static final String META_DATA = "metadata.";
	private static final String LAST_NAME = "customer.accountInfo.lastName";
	

	public Query buildQuery(MultiValueMap<String, String> searchParams, CollectionReference collectionRef, boolean encoded) {
		Query query = getLimit(searchParams, collectionRef);
		for(String key : searchParams.keySet()) {
			SearchableParams param = SearchableParams.getParamType(key);
			if (ObjectUtils.isNotEmpty(param)) {
				//LOGGER.debug("recived search param:{} corresponding metadata mapping:{}", param,MetaDataType.valueOf(param.name()));
				for(String item : searchParams.get(key)) {

					List<String> values = getValues(item, param, encoded);
					if (!CollectionUtils.isEmpty(values) && values.size() > 1) {
						//ORing scenario
						query = generateOrQuery(param, query, values);
					} else {
						// AND scenario
						query = generateAndQuery(param, query, values);
					}
				}
			} else {
				LOGGER.warn("Ignoring the invalid search parameter:{}", key);
			}
		}

		return query;
	}
	
	private Query generateOrQuery(SearchableParams param, Query query, List<String> values) {
		if(param == SearchableParams.LAST_NAME) {
			List<String> upperCasedVals = values.stream().map(StringUtils::upperCase).collect(Collectors.toList());
			return query.whereIn( LAST_NAME , upperCasedVals);
		} else {
			return query.whereIn(META_DATA + MetaDataType.valueOf(param.name()).name(), values);
		}
	}
	
	private Query generateAndQuery(SearchableParams param, Query query, List<String> values) {
		if(param == SearchableParams.LAST_NAME) {
			return query.whereEqualTo( LAST_NAME , StringUtils.upperCase(values.get(0)));
		} else {
			//provincial card=pc1751
			//Query q = query.whereEqualTo("provincial card","pc1751").get;
			String abc = META_DATA + MetaDataType.valueOf(param.name()).name()+"="+ values.get(0);
			return query.whereEqualTo(META_DATA + MetaDataType.valueOf(param.name()).name(), values.get(0));
		}
	}

	private Query getLimit(MultiValueMap<String, String> searchParams, CollectionReference collectionRef) {
		Query query = null;
		if(ObjectUtils.isNotEmpty(searchParams.get(ApplicationConstants.LIMIT_KEY)) && NumberUtils.toInt(searchParams.get(ApplicationConstants.LIMIT_KEY).get(0))>0) {
			query = collectionRef.limit(NumberUtils.toInt(searchParams.get(ApplicationConstants.LIMIT_KEY).get(0)));
			searchParams.remove(ApplicationConstants.LIMIT_KEY);
		} else {
			query = collectionRef.limit(limit);
		}
		return query;
	}
	
	private List<String> getValues(String item, SearchableParams param, boolean encoded) {
		if (!encoded) {
			return Stream.of(StringUtils.split(item, ",")).collect(Collectors.toList());
		}
		return Stream.of(StringUtils.split(item, ","))
				.map(String::trim)
				.filter(StringUtils::isNotBlank)
				.map(val -> MetaDataType.isIdentificationTypeEncoded(param.name()) ? hash.sha512CaseInsensitive(val) : val)
				.collect(Collectors.toList());
	}
}
