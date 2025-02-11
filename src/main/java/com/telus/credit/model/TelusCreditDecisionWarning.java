package com.telus.credit.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_NULL)
public class TelusCreditDecisionWarning {
	  
   private String id;  
   
/**** to support V1 version ********/   
	@JsonIgnore
   private String warningHistoryId;
   public String getWarningHistoryId() {
	      return warningHistoryId;
	   }
   public void setWarningHistoryId(String val) {
      this.warningHistoryId = val;
   }
	@JsonIgnore
   private String warningDetectionDate;
   public String getWarningDetectionDate() {	   
	      return warningDetectionDate;
	   }
   public void setWarningDetectionDate(String val) {
	      this.warningDetectionDate = val;
	}   

   /**** end of to support V1 version ********/
   @NotBlank( message = "1116")
   private String warningCategoryCd;

   private String warningCd;

   private String warningTypeCd;

   private String warningItemTypeCd;

   @NotBlank( message = "1117")
   private String warningStatusCd;

   @NotBlank( message = "1118")
   

   private String warningDetectionTs;

   @Pattern(regexp = "(0)|(1)|(true)|(false)")
   private String resolvedPermanentlyInd;
   
   
   private Long approvalCreditAssessmentId;
  
   private String approvalTs;
   
   private Long approvalExternalId;
   //private String memoTypeCd;
   private TimePeriod validFor;
   
   public String getId() {
      return id;
   }
   public void setId(String warningHistoryId) {
      this.id = warningHistoryId;
   }
   public String getWarningCategoryCd() {
	   return removeQuotationMark(warningCategoryCd);
   }
   public void setWarningCategoryCd(String warningCategoryCd) {
      this.warningCategoryCd = warningCategoryCd;
   }

   public String getWarningCd() {
	   return removeQuotationMark(warningCd);
   }
   public void setWarningCd(String warningCd) {
      this.warningCd = warningCd;
   }

   public String getWarningTypeCd() {
	   return removeQuotationMark(warningTypeCd);
   }
   public void setWarningTypeCd(String warningTypeCd) {
      this.warningTypeCd = warningTypeCd;
   }

   public String getWarningItemTypeCd() {
	   return removeQuotationMark(warningItemTypeCd);
   }
   public void setWarningItemTypeCd(String warningItemTypeCd) {
      this.warningItemTypeCd = warningItemTypeCd;
   }

   public String getWarningDetectionTs() {	   
      return warningDetectionTs;
   }
   public void setWarningDetectionTs(String warningDetectionTs) {
      this.warningDetectionTs = warningDetectionTs;
   }
   public String getWarningStatusCd() {
	   return removeQuotationMark(warningStatusCd);
   }
   public void setWarningStatusCd(String warningStatusCd) {
      this.warningStatusCd = warningStatusCd;
   }

   public String getResolvedPermanentlyInd() {
	   return removeQuotationMark(resolvedPermanentlyInd);
   }
   public void setResolvedPermanentlyInd(String resolvedPermanentlyInd) {
      this.resolvedPermanentlyInd = resolvedPermanentlyInd;
   }
   public TimePeriod getValidFor() {
      return validFor;
   }
   public void setValidFor(TimePeriod validFor) {
      this.validFor = validFor;
   }


   public Long getApprovalCreditAssessmentId() {
      return approvalCreditAssessmentId;
   }
   public void setApprovalCreditAssessmentId(Long creditAssessmentId) {
      this.approvalCreditAssessmentId = creditAssessmentId;
   }
   public String getApprovalTs() {
      return approvalTs;
   }
   public void setApprovalTs(String approvalDate) {
      this.approvalTs = approvalDate;
   }
   public Long getApprovalExternalId() {
      return approvalExternalId;
   }
   public void setApprovalExternalId(Long approvalExternalId) {
      this.approvalExternalId = approvalExternalId;
   }

	/*
	 * public String getMemoTypeCd() { return removeQuotationMark(memoTypeCd); }
	 * public void setMemoTypeCd(String memoTypeCd) { this.memoTypeCd = memoTypeCd;
	 * }
	 */
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
