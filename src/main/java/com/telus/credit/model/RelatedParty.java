package com.telus.credit.model;

import org.apache.commons.lang3.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.telus.credit.model.common.PartyType;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class RelatedParty {


    private String id;
    private String role;
    private String href;
    private String name;
    @JsonProperty("@type")
    private String type;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonProperty("@referredType")
    private String atReferredType;
    @JsonProperty("@baseType")
    private String atBaseType;

 
	/*
	 * commented out due to firestore issue in converting interface to impl classes, Could not deserialize object. Class com.telus.credit.model.RelatedPartyInterface
	 
	 private RelatedPartyInterface engagedParty;
	 public void setEngagedParty(RelatedPartyInterface engagedParty) {
	 	this.engagedParty = engagedParty; 
	 }
	*/
        
    public RelatedPartyInterface getEngagedParty() {
    	//workaround due to firestore issue in converting interface to impl classes
    	//convert Individual or Organization to EngagedParty
    	if( !ObjectUtils.isEmpty(getIndividual())) {
			if (PartyType.INDIVIDUAL.equals(getIndividual().getRelatedPartyType())) {
				return getIndividual() ;
			}
    	}
		else {
			if( !ObjectUtils.isEmpty(getOrganization())) {
				if (PartyType.ORGANIZATION.equals(getOrganization().getRelatedPartyType())) {
					return  getOrganization() ; 
				}	
			}
		}    	
 	 return null;
 	}

    //************************    
    //do not include in response as engagedParty shall be provided
    @JsonIgnore
    private Individual individual;
	public void setIndividual(Individual val) {
		this.individual=val;	
	}
    public Individual getIndividual() {
        return this.individual;
    }
    
    //do not include in response as engagedParty shall be provided
    @JsonIgnore
    private Organization organization;
	public void setOrganization(Organization val) {
		this.organization=val;	
	}	
    public Organization getOrganization() {
        return this.organization;
    }	
  //************************       
    
    public String getId() {
        return id;
    }

    public void setId(String customerId) {
        this.id = customerId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getAtReferredType() {
        return atReferredType;
    }

    public void setAtReferredType(String atReferredType) {
        this.atReferredType = atReferredType;
    }

    public String getAtBaseType() {
        return atBaseType;
    }

    public void setAtBaseType(String atBaseType) {
        this.atBaseType = atBaseType;
    }
}
