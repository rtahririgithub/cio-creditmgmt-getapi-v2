package com.telus.credit.firestore.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AssesmentDocumentCompact {
	
	private String customerId;
	private String assessmentMessageCd;
	private String creditAssessmentTypeCd;
	private String creditAssessmentSubTypeCd;
	private String lineOfBusiness;
	
	public String getLineOfBusiness() {
		return lineOfBusiness;
	}


	public void setLineOfBusiness(String lineOfBusiness) {
		this.lineOfBusiness = lineOfBusiness;
	}


	public String getCustomerId() {
		return customerId;
	}


	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}


	public String getAssessmentMessageCd() {
		return assessmentMessageCd;
	}


	public void setAssessmentMessageCd(String assessmentMessageCd) {
		this.assessmentMessageCd = assessmentMessageCd;
	}

	public String getCreditAssessmentTypeCd() {
		return creditAssessmentTypeCd;
	}


	public void setCreditAssessmentTypeCd(String creditAssessmentTypeCd) {
		this.creditAssessmentTypeCd = creditAssessmentTypeCd;
	}

	public String getCreditAssessmentSubTypeCd() {
		return creditAssessmentSubTypeCd;
	}


	public void setCreditAssessmentSubTypeCd(String creditAssessmentSubTypeCd) {
		this.creditAssessmentSubTypeCd = creditAssessmentSubTypeCd;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
}
