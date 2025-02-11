package com.telus.credit.model;


import com.telus.credit.common.serializer.CustomDecimalSerializer;
import com.telus.credit.validation.UniqueIdentification;
import com.telus.credit.validation.ValidCreditClass;
import com.telus.credit.validation.ValidCreditProgramName;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.cloud.firestore.annotation.Exclude;
import com.telus.credit.common.serializer.CustomDecimalSerializer;
import com.telus.credit.common.serializer.StringToLongSerializer;
import com.telus.credit.model.common.ApplicationConstants;
import com.telus.credit.validation.ValidNumber;
import com.telus.credit.validation.ValidTimePeriod;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class CreditProfile {
   
   private String id;
   
   @JsonProperty("creditProfileLegacyId")   
   @JsonInclude
   private Long creditProfileLegacyId;
   
   private String creationTs;

   @JsonSerialize(using = StringToLongSerializer.class)
   @ValidNumber(min = 0,  message = "1124")
   private String creditRiskLevelNum;
   
   
   private String creditRiskRating;
   

   @JsonSerialize(using = StringToLongSerializer.class)
   @ValidNumber(message = "Invalid number")
   private String primaryCreditScoreCd;

   @ValidTimePeriod
   private TimePeriod validFor;
   private String primaryCreditScoreTypeCd;
   //@ValidDecisionCode(groups = {Create.class, Patch.class})
   private String bureauDecisionCd;
   
   private String bureauDecisionCdTxtEn;
   
   private String bureauDecisionCdTxtFr;
   
   private String creditProgramName;
   
   private String creditCheckConsentCd;
   
   private String applicationProvinceCd;
   
   private String lineOfBusiness;

   private String creditClassCd;
   

   private String creditClassTs;

   private String creditDecisionCd;
   
   private String creditDecisionTs;
   
   private String creditRiskLevelDecisionCd;
   

   private String creditRiskLevelTs;

   @JsonSerialize(using = CustomDecimalSerializer.class)
   private BigDecimal clpRatePlanAmt;

   private Integer clpContractTermNum;

   @JsonSerialize(using = CustomDecimalSerializer.class)
   private BigDecimal clpCreditLimitAmt;
   
   @JsonSerialize(using = CustomDecimalSerializer.class)
   private BigDecimal averageSecurityDepositAmt;

   @JsonProperty("riskLevelRiskAssessment")
   private RiskLevelRiskAssessment riskLevelRiskAssessment;

   private RiskLevelRiskAssessment lastRiskAssessment;

   @Valid
   private List<TelusCreditDecisionWarning> warnings;

   private List<RelatedParty> relatedParty;

   @JsonProperty("attachments")
   private List<Attachments> attachments;

   @JsonProperty("channel")
   private TelusChannel channel;

   @JsonProperty("characteristic")
   private List<TelusCharacteristic> characteristic;

   private String statusCd;
   

   private String statusTs;
   
   private String customerCreditProfileRelCd;
   
  // private Long creditAssessmentId;

   //The properties would be removed on discussion
   @JsonProperty("createdBy")
   private String createdBy;

   @JsonProperty("createdTs")
   private String createdTs;

   private Boolean boltonInd;
   private List<ProductCategoryQualification> productCategoryQualification;

   public Boolean getBoltonInd() {
      return boltonInd;
   }

   public void setBoltonInd(Boolean boltonInd) {
      this.boltonInd = boltonInd;
   }

   public List<ProductCategoryQualification> getProductCategoryQualification() {
      return productCategoryQualification;
   }

   public void setProductCategoryQualification(List<ProductCategoryQualification> productCategoryQualification) {
      this.productCategoryQualification = productCategoryQualification;
   }
  
   public String getId() {
      return id;
   }
   public void setId(String id) {
      this.id = id;
   }

	// @JsonIgnore commented to support Wireline requirement to return creditProfileLegacyId to soap svc consumers.
	 public Long getCreditProfileLegacyId() { 
		 return creditProfileLegacyId; 
	 } 
	 public void setCreditProfileLegacyId(Long creditProfileLegacyId) { 
		 this.creditProfileLegacyId = creditProfileLegacyId;
	}
	 
	 
   @ApiModelProperty(value = "The date the profile was established", required = true, example = "2021-01-09T17:01:22.620Z")
   public String getCreationTs() {
      return creationTs;
   }

   public void setCreationTs(String creditProfileDate) {
      this.creationTs = creditProfileDate;
   }
   public String getCreditRiskLevelNum() {
      return creditRiskLevelNum;
   }
   public void setCreditRiskLevelNum(String creditRiskRating) {
      this.creditRiskLevelNum = creditRiskRating;
   }
   
   
   public String getCreditRiskRating() {
	      return creditRiskRating;
	   }   
   public void setCreditRiskRating(String creditRiskRating) {
	      this.creditRiskRating = creditRiskRating;
	}
   
   public String getPrimaryCreditScoreCd() {
      return primaryCreditScoreCd;
   }
   public void setPrimaryCreditScoreCd(String creditScore) {
      this.primaryCreditScoreCd = creditScore;
   }
   public TimePeriod getValidFor() {
      return validFor;
   }
   public void setValidFor(TimePeriod validFor) {
      this.validFor = validFor;
   }

   @JsonProperty("@baseType")
   @Exclude
   public String getBaseType() {
      return  ApplicationConstants.PROFILE_BASE_TYPE;
   }

   @Exclude
   public void setBaseType(String baseType) {
   }

   @JsonProperty("@type")
   @Exclude
   public String getType() {
      return ApplicationConstants.PROFILE_TYPE;
   }

   @Exclude
   public void setType(String type) {
   }

   public RiskLevelRiskAssessment getLastRiskAssessment() {
      return lastRiskAssessment;
   }

   public void setLastRiskAssessment(RiskLevelRiskAssessment lastRiskAssessment) {
      this.lastRiskAssessment = lastRiskAssessment;
   }

   public String getCreditCheckConsentCd() {
      return creditCheckConsentCd;
   }
   public void setCreditCheckConsentCd(String creditProfileConsentCd) {
      this.creditCheckConsentCd = creditProfileConsentCd;
   }

   public String getApplicationProvinceCd() {
      return applicationProvinceCd;
   }
   public void setApplicationProvinceCd(String applicationProvinceCd) {
      this.applicationProvinceCd = applicationProvinceCd;
   }

   public String getLineOfBusiness() {
       //fix for the ph1 (wireless) customers that did not have lineofbusiness populated
       if(lineOfBusiness==null || lineOfBusiness.trim().isEmpty()){
    	   lineOfBusiness="WIRELESS";
       };
      return lineOfBusiness;
   }
   public void setLineOfBusiness(String lineOfBusiness) {
      this.lineOfBusiness = lineOfBusiness;
   }

   public String getPrimaryCreditScoreTypeCd() {
      return primaryCreditScoreTypeCd;
   }
   public void setPrimaryCreditScoreTypeCd(String primaryCreditScoreTypeCd) {
      this.primaryCreditScoreTypeCd = primaryCreditScoreTypeCd;
   }

   public String getBureauDecisionCd() {
      return bureauDecisionCd;
   }
   public void setBureauDecisionCd(String bureauDecisionCode) {
      this.bureauDecisionCd = bureauDecisionCode;
   }
   @Exclude
   public String getBureauDecisionCdTxtEn() {
      return bureauDecisionCdTxtEn;
   }
   @Exclude
   public void setBureauDecisionCdTxtEn(String bureauDecisionMessage) {
      this.bureauDecisionCdTxtEn = bureauDecisionMessage;
   }

   @Exclude
   public String getBureauDecisionCdTxtFr() {
      return bureauDecisionCdTxtFr;
   }
   @Exclude
   public void setBureauDecisionCdTxtFr(String bureauDecisionMessage_fr) {
      this.bureauDecisionCdTxtFr = bureauDecisionMessage_fr;
   }

   public String getCreditProgramName() {
      return creditProgramName;
   }
   public void setCreditProgramName(String creditProgramName) {
      this.creditProgramName = creditProgramName;
   }
   public String getCreditClassCd() {
      return creditClassCd;
   }
   public void setCreditClassCd(String creditClassCd) {
      this.creditClassCd = creditClassCd;
   }
   public String getCreditClassTs() {
      return creditClassTs;
   }
   public void setCreditClassTs(String creditClassDate) {
      this.creditClassTs = creditClassDate;
   }
   public String getCreditDecisionCd() {
      return creditDecisionCd;
   }
   public void setCreditDecisionCd(String creditDecisionCd) {
      this.creditDecisionCd = creditDecisionCd;
   }

   public String getCreditDecisionTs() {
      return creditDecisionTs;
   }
   public void setCreditDecisionTs(String creditDecisionDate) {
      this.creditDecisionTs = creditDecisionDate;
   }

   public String getCreditRiskLevelDecisionCd() {
      return creditRiskLevelDecisionCd;
   }
   public void setCreditRiskLevelDecisionCd(String riskLevelDecisionCd) {
      this.creditRiskLevelDecisionCd = riskLevelDecisionCd;
   }

   public String getCreditRiskLevelTs() {
      return creditRiskLevelTs;
   }
   public void setCreditRiskLevelTs(String riskLevelDt) {
      this.creditRiskLevelTs = riskLevelDt;
   }

   public BigDecimal getClpRatePlanAmt() {
      return clpRatePlanAmt;
   }
   public void setClpRatePlanAmt(BigDecimal clpRatePlanAmt) {
      this.clpRatePlanAmt = clpRatePlanAmt;
   }

   public Integer getClpContractTermNum() {
      return clpContractTermNum;
   }
   public void setClpContractTermNum(Integer clpContractTerm) {
      this.clpContractTermNum = clpContractTerm;
   }

   public BigDecimal getClpCreditLimitAmt() {
      return clpCreditLimitAmt;
   }
   public void setClpCreditLimitAmt(BigDecimal clpCreditLimitAmt) {
      this.clpCreditLimitAmt = clpCreditLimitAmt;
   }

   public BigDecimal getAverageSecurityDepositAmt() {
      return averageSecurityDepositAmt;
   }
   public void setAverageSecurityDepositAmt(BigDecimal averageSecurityDepositAmt) {
      this.averageSecurityDepositAmt = averageSecurityDepositAmt;
   }

   public List<TelusCreditDecisionWarning> getWarnings() {
      return warnings;
   }
   public void setWarnings(List<TelusCreditDecisionWarning> warningHistoryList) {
      this.warnings = warningHistoryList;
   }

   public List<RelatedParty> getRelatedParty() {
      return relatedParty;
   }

   public void setRelatedParty(List<RelatedParty> relatedParties) {
      this.relatedParty = relatedParties;
   }

   public List<Attachments> getAttachments() {
      return attachments;
   }

   public void setAttachments(List<Attachments> attachments) {
      this.attachments = attachments;
   }

   public String getStatusCd(){
      return statusCd;
   }

   public void setStatusCd(String creditProfileStatusCd) {
      this.statusCd = creditProfileStatusCd;
   }

   public String getStatusTs() {
      return statusTs;
   }

   public void setStatusTs(String creditProfileStatusTs) {
      this.statusTs = creditProfileStatusTs;
   }

   public String getCustomerCreditProfileRelCd() {
      return customerCreditProfileRelCd;
   }

   public void setCustomerCreditProfileRelCd(String customerCreditProfileRelCd) {
      this.customerCreditProfileRelCd = customerCreditProfileRelCd;
   }

   public RiskLevelRiskAssessment getRiskLevelRiskAssessment() {
      return riskLevelRiskAssessment;
   }

   public void setRiskLevelRiskAssessment(RiskLevelRiskAssessment riskLevelRiskAssessment) {
      this.riskLevelRiskAssessment = riskLevelRiskAssessment;
   }

	/*
	 * public Long getCreditAssessmentId() { return creditAssessmentId; }
	 * 
	 * public void setCreditAssessmentId(Long creditAssessmentId) {
	 * this.creditAssessmentId = creditAssessmentId; }
	 */

   public String getCreatedBy() {
      return createdBy;
   }

   public void setCreatedBy(String createdBy) {
      this.createdBy = createdBy;
   }

   public String getCreatedTs() {
      return createdTs;
   }

   public void setCreatedTs(String createdTs) {
      this.createdTs = createdTs;
   }

   public TelusChannel getChannel() {
      return channel;
   }

   public void setChannel(TelusChannel channel) {
      this.channel = channel;
   }

   public List<TelusCharacteristic> getCharacteristic() {
      return characteristic;
   }

   public void setCharacteristic(List<TelusCharacteristic> characteristicsList) {
      this.characteristic = characteristicsList;
   }

   @Override
   public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
}
