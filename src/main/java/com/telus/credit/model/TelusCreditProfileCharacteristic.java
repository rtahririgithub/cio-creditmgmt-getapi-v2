package com.telus.credit.model;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.cloud.firestore.annotation.Exclude;
import com.telus.credit.common.serializer.CustomDecimalSerializer;
import com.telus.credit.validation.ValidCreditClass;
import com.telus.credit.validation.ValidCreditProgramName;

@JsonInclude(Include.NON_NULL)
public class TelusCreditProfileCharacteristic {

   private Long creditProfileLegacyId;
   
   private String primaryCreditScoreCd;
   private String primaryCreditScoreTypeCd;
   //@ValidDecisionCode
   private String bureauDecisionCode;
   private String bureauDecisionMessage;
   private String bureauDecisionMessage_fr;
   @ValidCreditProgramName
   private String creditProgramName;

   @ValidCreditClass
   private String creditClassCd;

   private String creditClassDate;

   private String creditDecisionCd;

   private String creditDecisionDate;
  // @JsonSerialize(using = StringToLongSerializer.class)
   private String riskLevelNumber;

   private String riskLevelDecisionCd;

   private String riskLevelDt;

   @JsonSerialize(using = CustomDecimalSerializer.class)
   private BigDecimal clpRatePlanAmt;

   //@JsonSerialize(using = StringToLongSerializer.class)
   private Integer clpContractTerm;

   @JsonSerialize(using = CustomDecimalSerializer.class)
   private BigDecimal clpCreditLimit;
   @JsonSerialize(using = CustomDecimalSerializer.class)
   private BigDecimal averageSecurityDepositAmt;
   private String assessmentMessageCode;
   private String assessmentMessage;
   private String assessmentMessage_fr;
   private Long creditAssessmentId;
   @Valid
   private List<TelusCreditDecisionWarning> warningHistoryList;


   public String getPrimaryCreditScoreCd() {
	   return removeQuotationMark(primaryCreditScoreCd);
   }
   public void setPrimaryCreditScoreCd(String primaryCreditScoreCd) {
      this.primaryCreditScoreCd = primaryCreditScoreCd;
   }
   public String getPrimaryCreditScoreTypeCd() {
	   return removeQuotationMark(primaryCreditScoreTypeCd);
   }
   public void setPrimaryCreditScoreTypeCd(String primaryCreditScoreTypeCd) {
      this.primaryCreditScoreTypeCd = primaryCreditScoreTypeCd;
   }
   public String getBureauDecisionCode() {
      return removeQuotationMark(bureauDecisionCode);
   }
   public void setBureauDecisionCode(String bureauDecisionCode) {
      this.bureauDecisionCode = bureauDecisionCode;
   }
   @Exclude
   public String getBureauDecisionMessage() {
      return removeQuotationMark(bureauDecisionMessage);
   }
   @Exclude
   public void setBureauDecisionMessage(String bureauDecisionMessage) {
      this.bureauDecisionMessage = bureauDecisionMessage;
   }

   @Exclude
   public String getBureauDecisionMessage_fr() {
      return bureauDecisionMessage_fr;
   }
   @Exclude
   public void setBureauDecisionMessage_fr(String bureauDecisionMessage_fr) {
      this.bureauDecisionMessage_fr = bureauDecisionMessage_fr;
   }

   public String getCreditProgramName() {
	   return removeQuotationMark(creditProgramName);
   }
   public void setCreditProgramName(String creditProgramName) {
      this.creditProgramName = creditProgramName;
   }
   public String getCreditClassCd() {	   
      return removeQuotationMark(creditClassCd);
   }
   public void setCreditClassCd(String creditClassCd) {
      this.creditClassCd = creditClassCd;
   }
   public String getCreditClassDate() {
      return creditClassDate;
   }
   public void setCreditClassDate(String creditClassDate) {
      this.creditClassDate = creditClassDate;
   }
   public String getCreditDecisionCd() {
	   return removeQuotationMark(creditDecisionCd);
   }
   public void setCreditDecisionCd(String creditDecisionCd) {
      this.creditDecisionCd = creditDecisionCd;
   }

   public String getCreditDecisionDate() {
      return creditDecisionDate;
   }
   public void setCreditDecisionDate(String creditDecisionDate) {
      this.creditDecisionDate = creditDecisionDate;
   }
   public String getRiskLevelNumber() {
      return riskLevelNumber;
   }
   public void setRiskLevelNumber(String riskLevelNumber) {
      this.riskLevelNumber = riskLevelNumber;
   }

   public String getRiskLevelDecisionCd() {
	   return removeQuotationMark(riskLevelDecisionCd);
   }
   public void setRiskLevelDecisionCd(String riskLevelDecisionCd) {
      this.riskLevelDecisionCd = riskLevelDecisionCd;
   }

   public String getRiskLevelDt() {
      return riskLevelDt;
   }
   public void setRiskLevelDt(String riskLevelDt) {
      this.riskLevelDt = riskLevelDt;
   }
   public BigDecimal getClpCreditLimit() {
      return clpCreditLimit;
   }
   public void setClpCreditLimit(BigDecimal clpCreditLimit) {
      this.clpCreditLimit = clpCreditLimit;
   }

   public BigDecimal getClpRatePlanAmt() {
      return clpRatePlanAmt;
   }
   public void setClpRatePlanAmt(BigDecimal clpRatePlanAmt) {
      this.clpRatePlanAmt = clpRatePlanAmt;
   }

   public Integer getClpContractTerm() {
      return clpContractTerm;
   }
   public void setClpContractTerm(Integer clpContractTerm) {
      this.clpContractTerm = clpContractTerm;
   }

   public BigDecimal getAverageSecurityDepositAmt() {
      return averageSecurityDepositAmt;
   }
   public void setAverageSecurityDepositAmt(BigDecimal averageSecurityDepositAmt) {
      this.averageSecurityDepositAmt = averageSecurityDepositAmt;
   }
   public String getAssessmentMessageCode() {
      return assessmentMessageCode;
   }
   public void setAssessmentMessageCode(String assessmentMessageCode) {
      this.assessmentMessageCode = assessmentMessageCode;
   }

   @Exclude
   public String getAssessmentMessage() {
      return removeQuotationMark(assessmentMessage);
   }
   @Exclude
   public void setAssessmentMessage(String assessmentMessage) {
      this.assessmentMessage = assessmentMessage;
   }

   @Exclude
   public String getAssessmentMessage_fr() {
      return assessmentMessage_fr;
   }
   @Exclude
   public void setAssessmentMessage_fr(String assessmentMessage_fr) {
      this.assessmentMessage_fr = assessmentMessage_fr;
   }

   public List<TelusCreditDecisionWarning> getWarningHistoryList() {
      return warningHistoryList;
   }
   public void setWarningHistoryList(List<TelusCreditDecisionWarning> warningHistoryList) {
      this.warningHistoryList = warningHistoryList;
   }

	// @JsonIgnore commented to support Wireline requirement to return creditProfileLegacyId to soap svc consumers.
   public Long getCreditProfileLegacyId() {
      return creditProfileLegacyId;
   }
   public void setCreditProfileLegacyId(Long creditProfileLegacyId) {
      this.creditProfileLegacyId = creditProfileLegacyId;
   }
   public Long getCreditAssessmentId() {
      return creditAssessmentId;
   }
   public void setCreditAssessmentId(Long creditAssessmentId) {
      this.creditAssessmentId = creditAssessmentId;
   }

   public  String removeQuotationMark(String val) {
   	try{
   		val=(val!=null)?val.replaceAll("\"", ""):"";
   	}
   	catch(Exception e){
   		e.printStackTrace();
   	}
   	return val;
   }
   @Override
   public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
   }
}
