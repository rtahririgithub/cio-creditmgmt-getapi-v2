package com.telus.credit.model.common;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;


/**
 * Mapped Identification type text to code. This code will be stored in DB
	PSP("Passport"), 
	PRV("Provincial Card"),
	SIN("Social Insurance Number"), 
	CC("Credit Card"), 
	DL("Drivers License"),
	HC("Health Card"),
	
	BIC("Business Registration Number"), 
	CRA("CRA Business Number"),
	NSJ("Nova Scotia Joint Stocks Registry"), 
	QST("QST Registration Number for Quebec"),

 */
public enum IdentificationType {

	PSP("PSP"), 
	PRV("PRV"),
	SIN("SIN"), 
	CC("CC"), 
	DL("DL"),
	HC("HC"),
	
	BIC("BIC"), 
	CRA("CRA"),
	NSJ("NSJ"), 
	QST("QST"),

	;
	


	private final String desc;	
	IdentificationType(final String desc) {
		this.desc = desc;
	}
	public String getDesc() {
		return desc;
	}

	

	private static final Map<String, IdentificationType> lookup = new HashMap<>();
	static {
		for (final IdentificationType type : IdentificationType.values()) {
			lookup.put(StringUtils.lowerCase(type.desc), type);
		}
	}
	private static final Map<String, IdentificationType> lookupph1 = new HashMap<>();
	static {
		 {
			lookupph1.put(StringUtils.lowerCase("Passport"),IdentificationType.PSP); 
			lookupph1.put(StringUtils.lowerCase("Provincial Card"),IdentificationType.PRV); 
			lookupph1.put(StringUtils.lowerCase("Social Insurance Number"),IdentificationType.SIN); 
			lookupph1.put(StringUtils.lowerCase("Credit Card"),IdentificationType.CC); 
			lookupph1.put(StringUtils.lowerCase("Drivers License"),IdentificationType.DL); 
			lookupph1.put(StringUtils.lowerCase("Health Card"),IdentificationType.HC); 
			lookupph1.put(StringUtils.lowerCase("Business Registration Number"),IdentificationType.BIC); 
			lookupph1.put(StringUtils.lowerCase("CRA Business Number"),IdentificationType.CRA);  
			lookupph1.put(StringUtils.lowerCase("Nova Scotia Joint Stocks Registry"),IdentificationType.NSJ);  
			lookupph1.put(StringUtils.lowerCase("QST Registration Number for Quebec"),IdentificationType.QST);  

		}
	}
	
	public static IdentificationType getIdentificationType(String descOrName) {

		IdentificationType identificationType = lookup.get(StringUtils.lowerCase(descOrName));
		if(identificationType==null ) {
			identificationType = lookupph1.get(StringUtils.lowerCase(descOrName));
		}
		
		if (identificationType == null) {
			for (IdentificationType value : values()) {
				if (value.name().equalsIgnoreCase(descOrName)) {
					return value;
				}
			}
		}

		return identificationType;
	}

	@Override
	public String toString() {
		//return desc;
		return name();
	}
}
