package com.telus.credit.firestore.model;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.PropertyName;
import com.telus.credit.model.Customer;

@JsonInclude(Include.NON_NULL)
public class CustomerDocument {
	
	
	private Map<String,Object> metaData;
	
	private Customer customer;
	
	private String firestoreId;
	
	private long lastUpdateTimeInNanos; 
	
	private long publishTimeinNanos;

	@PropertyName("metadata")
	public Map<String,Object> getMetaData() {
		return metaData;
	}

	@PropertyName("metadata")
	public void setMetaData(Map<String,Object> metaData) {
		this.metaData = metaData;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	@Exclude
	public String getFirestoreId() {
		return firestoreId;
	}

	@Exclude
	public void setFirestoreId(String firestoreId) {
		this.firestoreId = firestoreId;
	}

	@Exclude
	public long getLastUpdateTimeInNanos() {
		return lastUpdateTimeInNanos;
	}

	@Exclude
	public void setLastUpdateTimeInNanos(long lastUpdateTimeInNanos) {
		this.lastUpdateTimeInNanos = lastUpdateTimeInNanos;
	}
	
	public long getPublishTimeinNanos() {
		return publishTimeinNanos;
	}

	public void setPublishTimeinNanos(long publishTimeinNanos) {
		this.publishTimeinNanos = publishTimeinNanos;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
