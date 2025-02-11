package com.telus.credit.model.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public enum SearchableParams {
	

	CUSTOMER_ID("customerId"),
	UUID("uuid"),
	USERID("userId"),

	POSTAL_CODE("postalCode"), 
	LAST_NAME("lastName"), 
	LOB("LOB"),
//individual	
	BIRTH_DATE("birthDate"), 
	SIN("SIN"), 
	CC("CC"), 
	DL("DL"),
	PRV("PRV"),
	HC("HC"),	
	PSP("PSP"),
	
//organizations	
	BIC("BIC"), 
	CRA("CRA"),
	NSJ("NSJ"), 
	QST("QST"),
	
	MATCHEDBYCARDID("MATCHEDBYCARDID"), 
;

	SearchableParams(final String param) {
		this.params = param;
	}

	public String getSearchableParams() {
		return params;
	}

	private final String params;

	private static final Map<String, SearchableParams> lookup = new HashMap<>();

	static {
		for (final SearchableParams type : SearchableParams.values()) {
			lookup.put(StringUtils.lowerCase(type.params), type);
		}
	}

	public static SearchableParams getParamType(String type) {
		return lookup.get(StringUtils.lowerCase(type));
	}
}
