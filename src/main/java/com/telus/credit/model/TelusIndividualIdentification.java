package com.telus.credit.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.firestore.annotation.Exclude;
import com.telus.credit.model.common.ApplicationConstants;
import com.telus.credit.model.common.IdentificationType;
import com.telus.credit.validation.ValidEnum;
import com.telus.credit.validation.ValidTimePeriod;

@JsonInclude(Include.NON_NULL)
public class TelusIndividualIdentification implements IdentificationInterface {
	@NotBlank(message = "1123")
	private String identificationId; // DL number if type is DL and refer province code from
										// TelusIndividualIdentificationCharacteristic
	private String identificationIdHashed;
	@ValidEnum(enumClass = IdentificationType.class, useToString = true, message = "1111")
	@NotBlank(message = "1111")
	private String identificationType;
	private String issuingAuthority;
	private String issuingDate;
	@ValidTimePeriod
	private TimePeriod validFor;
	private String schemaLocation;
	@Valid
	private TelusIndividualIdentificationCharacteristic telusCharacteristic;

	
	public String getIdentificationId() {
		return identificationId;
	}

	public void setIdentificationId(String identificationId) {
		this.identificationId = identificationId;
	}

	public String getIdentificationType() {
		return identificationType;
	}

	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}

	public String getIssuingAuthority() {
		return issuingAuthority;
	}

	public void setIssuingAuthority(String issuingAuthority) {
		this.issuingAuthority = issuingAuthority;
	}

	public String getIssuingDate() {
		return issuingDate;
	}

	public void setIssuingDate(String issuingDate) {
		this.issuingDate = issuingDate;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public void setValidFor(TimePeriod validFor) {
		this.validFor = validFor;
	}

	@JsonProperty("@baseType")
	public String getBaseType() {
		return ApplicationConstants.INDIVIDUAL_IDENTIFICATION_BASE_TYPE;
	}

	public void setBaseType(String baseType) {
	}

	@JsonProperty("@schemaLocation")
	@JsonIgnore
	@Exclude
	public String getSchemaLocation() {
		return schemaLocation;
	}

	@Exclude
	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

	@JsonProperty("@type")
	public String getType() {
		return ApplicationConstants.INDIVIDUAL_IDENTIFICATION_TYPE;
	}

	public void setType(String type) {
	}

	public TelusIndividualIdentificationCharacteristic getTelusCharacteristic() {
		return telusCharacteristic;
	}

	public void setTelusCharacteristic(TelusIndividualIdentificationCharacteristic telusCharacteristic) {
		this.telusCharacteristic = telusCharacteristic;
	}

	
	@JsonIgnore
	public String getIdentificationIdHashed() {
		return identificationIdHashed;
	}

	public void setIdentificationIdHashed(String identificationIdHashed) {
		this.identificationIdHashed = identificationIdHashed;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
}
