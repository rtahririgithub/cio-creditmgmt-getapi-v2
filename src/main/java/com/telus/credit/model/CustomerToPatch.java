package com.telus.credit.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.telus.credit.validation.ValidIdentification;

public class CustomerToPatch {

   @NotEmpty( message = "1100")
   @NotNull( message = "1100")
   @Valid
   private List<TelusCreditProfile> creditProfile;

   @NotNull(message = "1108")
   @ValidIdentification
   @Valid
   private RelatedPartyToPatch engagedParty;

   @NotNull(message = "1113")
   @Valid
   private TelusAuditCharacteristic telusAuditCharacteristic;


   public List<TelusCreditProfile> getCreditProfile() {
      return creditProfile;
   }
   public void setCreditProfile(List<TelusCreditProfile> creditProfile) {
      this.creditProfile = creditProfile;
   }

   public RelatedPartyToPatch getEngagedParty() {
      return engagedParty;
   }
   public void setEngagedParty(RelatedPartyToPatch engagedParty) {
      this.engagedParty = engagedParty;
   }

   public TelusAuditCharacteristic getTelusAuditCharacteristic() {
      return telusAuditCharacteristic;
   }
   public void setTelusAuditCharacteristic(TelusAuditCharacteristic telusAuditCharacteristic) {
      this.telusAuditCharacteristic = telusAuditCharacteristic;
   }


   @Override
   public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
 
}
