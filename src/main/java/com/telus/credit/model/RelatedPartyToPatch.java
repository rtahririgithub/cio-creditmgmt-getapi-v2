package com.telus.credit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telus.credit.model.common.PartyType;
import com.telus.credit.validation.UniqueIdentification;
import com.telus.credit.validation.ValidBirthDate;
import com.telus.credit.validation.ValidEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

public class RelatedPartyToPatch {

   private static final String BIRTHDT_PT = "(19|[2-9][0-9])[0-9]{2}\\-[0-9]{2}\\-[0-9]{2}";

   private String id;
   private String role;
   @NotBlank( message = "1103")
   @ValidEnum(enumClass = PartyType.class, useToString = true, message = "1103")
   private String atReferredType;
   @Pattern(regexp = BIRTHDT_PT, message = "1104")
   @ValidBirthDate
   private String birthDate;
   @Valid
   private List<ContactMedium> contactMedium;
   @Valid
   @UniqueIdentification
   private List<TelusIndividualIdentification> individualIdentification;
   @Valid
   @UniqueIdentification
   private List<OrganizationIdentification> organizationIdentification;

   @JsonProperty("characteristic")
   private List<TelusCharacteristic> characteristic;

   public String getId() {
      return id;
   }
   public void setId(String id) {
      this.id = id;
   }

   public String getRole() {
      return role;
   }

   public void setRole(String role) {
      this.role = role;
   }

   @JsonProperty("@referredType")
   public String getAtReferredType() {
      return atReferredType;
   }
   public void setAtReferredType(String atReferredType) {
      this.atReferredType = atReferredType;
   }
   
   public String getBirthDate() {
      return birthDate;
   }
   public void setBirthDate(String birthDate) {
      this.birthDate = birthDate;
   }
   public List<ContactMedium> getContactMedium() {
      return contactMedium;
   }
   public void setContactMedium(List<ContactMedium> contactMedium) {
      this.contactMedium = contactMedium;
   }
   public List<TelusIndividualIdentification> getIndividualIdentification() {
      return individualIdentification;
   }
   public void setIndividualIdentification(List<TelusIndividualIdentification> individualIdentification) {
      this.individualIdentification = individualIdentification;
   }
   public List<OrganizationIdentification> getOrganizationIdentification() {
      return organizationIdentification;
   }
   public void setOrganizationIdentification(List<OrganizationIdentification> organizationIdentification) {
      this.organizationIdentification = organizationIdentification;
   }

   public List<TelusCharacteristic> getCharacteristic() {
      return characteristic;
   }

   public void setCharacteristic(List<TelusCharacteristic> characteristicList) {
      this.characteristic = characteristicList;
   }

   @Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
}
