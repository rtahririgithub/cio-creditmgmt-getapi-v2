package com.telus.credit.service.impl;

import static com.telus.credit.common.PdsRefConstants.ASSESSMENT_MSG_VALUE_CODE;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.telus.credit.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.telus.credit.common.CommonHelper;
import com.telus.credit.common.PdsRefConstants;
import com.telus.credit.crypto.service.CryptoService;
import com.telus.credit.model.common.ApplicationConstants;
import com.telus.credit.model.common.PartyType;
import com.telus.credit.pds.model.Key;
import com.telus.credit.pds.model.MultiKeyReferenceDataItem;
import com.telus.credit.pds.service.MultiKeyReferenceDataService;
import com.telus.credit.pds.service.ReferenceDataService;

@Service
public class ResponseInterceptorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseInterceptorService.class);

    private CryptoService cryptoService;

    private ReferenceDataService referenceDataService;

    public ResponseInterceptorService(CryptoService cryptoService,
                                      ReferenceDataService referenceDataService) {
        this.cryptoService = cryptoService;
        this.referenceDataService = referenceDataService;
    }

    /**
     * Resolve fields values which were not stored in DB. Decrypt encrypted values
     *
     * @param customer
     * @param lang
     */
    public void resolveMissingFields(Customer customer, String lang) {
        if (customer == null) {
            return;
        }
        //support ph1 existing data 
        decrypt(customer.getIndividual());
        decrypt(customer.getOrganisation());
        
       // decrypt(customer.getEngagedParty()); ph2.1 moved EngagedParty (Org/Indv) to relatedparty(customer)
        
        // Get only non expired profiles! Ph2.1: get all profiles( expired and non-expired)
       // List<TelusCreditProfile> profiles = CommonHelper.nullSafe(customer.getCreditProfile()).stream().filter(profile -> ObjectUtils.isEmpty(profile.getValidFor()) || StringUtils.isEmpty(profile.getValidFor().getEndDateTime())).collect(Collectors.toList());
        //customer.setCreditProfile(profiles);
        for (TelusCreditProfile cp : CommonHelper.nullSafe(customer.getCreditProfile())) {
            decrypt(cp);
            //firestore data in ph1 format 
            if (cp.getTelusCharacteristic() != null) {
                populateDecisionCode(cp.getTelusCharacteristic());
                populateAssessmentMessage(cp.getTelusCharacteristic());
            }else {
              //firestore data in ph2 format 
                if (cp.getLastRiskAssessment()!= null) {
                	populateAssessmentMessage(cp.getLastRiskAssessment());	
                }
                populateDecisionCode(cp);
            }
        }
    }    
    private void decrypt(TelusCreditProfile creditProfile) {
        if (creditProfile == null) {
            return;
        }
        
        long start = System.currentTimeMillis();
        List<Attachments> attachments = CommonHelper.nullSafe(creditProfile.getAttachments()).stream()
                .map(attachment -> {
                    attachment.setContent(cryptoService.decryptAndIgnoreError(attachment.getContent()));
                    return attachment;
                }).collect(Collectors.toList());
        creditProfile.setAttachments(attachments);

        //decrypt TelusCreditProfileCharacteristic values
        if (creditProfile.getTelusCharacteristic() != null) {
            TelusCreditProfileCharacteristic characteristic = creditProfile.getTelusCharacteristic();
           
        	String riskLevelNumberV1 = characteristic.getRiskLevelNumber();
        	riskLevelNumberV1=(riskLevelNumberV1!=null && !riskLevelNumberV1.equalsIgnoreCase("null"))?riskLevelNumberV1.trim():"";
        	riskLevelNumberV1=(riskLevelNumberV1.isEmpty())?creditProfile.getCreditRiskRating():riskLevelNumberV1;        	          
            characteristic.setRiskLevelNumber(cryptoService.decryptAndIgnoreError(riskLevelNumberV1));
            
            characteristic.setPrimaryCreditScoreCd(cryptoService.decryptAndIgnoreError(characteristic.getPrimaryCreditScoreCd()));           
            characteristic.setCreditClassCd(cryptoService.decryptAndIgnoreError(characteristic.getCreditClassCd()));
            characteristic.setCreditDecisionCd(cryptoService.decryptAndIgnoreError(characteristic.getCreditDecisionCd()));
            characteristic.setRiskLevelDecisionCd(cryptoService.decryptAndIgnoreError(characteristic.getRiskLevelDecisionCd()));
           
            List<TelusCreditDecisionWarning> warnings = new ArrayList<TelusCreditDecisionWarning>();
            if( "WIRELINE".equalsIgnoreCase(creditProfile.getLineOfBusiness()) ){
            	warnings = CommonHelper.nullSafe(creditProfile.getWarnings()).stream().collect(Collectors.toList());
            }else {
            	//don't return warnings with EndDateTime
   	             warnings = CommonHelper.nullSafe(characteristic.getWarningHistoryList()).stream()
   	  	            .filter(
   	  	            		warn -> ObjectUtils.isEmpty(warn.getValidFor()) 
   	  	            		|| StringUtils.isEmpty(warn.getValidFor().getEndDateTime())
   	  	            		)
   	  	            		.collect(Collectors.toList());           		
            }
            
         // Get only non expired warnings!
            characteristic.setWarningHistoryList(warnings);
            for (TelusCreditDecisionWarning warning : warnings) {
                decrypt(warning);
            }
        }

    
    	//decrypt creditProfile values
        creditProfile.setPrimaryCreditScoreCd(cryptoService.decryptAndIgnoreError(creditProfile.getPrimaryCreditScoreCd()));
        creditProfile.setCreditRiskLevelNum(cryptoService.decryptAndIgnoreError(creditProfile.getCreditRiskLevelNum()));
        creditProfile.setCreditClassCd(cryptoService.decryptAndIgnoreError(creditProfile.getCreditClassCd()));
        creditProfile.setCreditDecisionCd(cryptoService.decryptAndIgnoreError(creditProfile.getCreditDecisionCd()));
        creditProfile.setCreditRiskLevelDecisionCd(cryptoService.decryptAndIgnoreError(creditProfile.getCreditRiskLevelDecisionCd()));
        // Get only non expired warnings!
        List<TelusCreditDecisionWarning> warnings = new ArrayList<TelusCreditDecisionWarning>();
        if( "WIRELINE".equalsIgnoreCase(creditProfile.getLineOfBusiness()) ){
            warnings = CommonHelper.nullSafe(creditProfile.getWarnings()).stream().collect(Collectors.toList());
        	
        }
        else {
        	//don't return warnings with EndDateTime
            warnings = CommonHelper.nullSafe(creditProfile.getWarnings()).stream()
                    .filter(
                    		warn -> ObjectUtils.isEmpty(warn.getValidFor()) 
                    		|| StringUtils.isEmpty(warn.getValidFor().getEndDateTime())
                    ).collect(Collectors.toList());
         	
        }
        creditProfile.setWarnings(warnings);
        for (TelusCreditDecisionWarning warning : warnings) {
            decrypt(warning);
        }
    
        List<RelatedParty> relatedPartyList = creditProfile.getRelatedParty();
        if(relatedPartyList!=null) {
	        for (RelatedParty relatedParty : relatedPartyList) {
	        	if( "customer".equalsIgnoreCase(relatedParty.getRole()) ) {
	        		decrypt(relatedParty.getEngagedParty());
	        	}
			}
        }
        
        LOGGER.debug("Decrypt credit profile took {}ms", System.currentTimeMillis() - start);
    }

    private void decrypt(TelusCreditDecisionWarning decisionWarning) {
        decisionWarning.setWarningStatusCd(cryptoService.decryptAndIgnoreError(decisionWarning.getWarningStatusCd()));
        decisionWarning.setWarningCd(cryptoService.decryptAndIgnoreError(decisionWarning.getWarningCd()));
        decisionWarning.setWarningCategoryCd(cryptoService.decryptAndIgnoreError(decisionWarning.getWarningCategoryCd()));
        decisionWarning.setWarningTypeCd(cryptoService.decryptAndIgnoreError(decisionWarning.getWarningTypeCd()));
        decisionWarning.setWarningItemTypeCd(cryptoService.decryptAndIgnoreError(decisionWarning.getWarningItemTypeCd()));
    }

    private void decrypt(RelatedPartyInterface engagedParty) {
        if (engagedParty == null) {
            return;
        }

        long start = System.currentTimeMillis();

        if (PartyType.INDIVIDUAL.equals(engagedParty.getRelatedPartyType())) {
            List<TelusIndividualIdentification> identifications = ((Individual) engagedParty).getIndividualIdentification();
            for (TelusIndividualIdentification identification : CommonHelper.nullSafe(identifications)) {
                identification.setIdentificationId(cryptoService.decryptAndIgnoreError(identification.getIdentificationId()));
            }
        } else {
            List<OrganizationIdentification> identifications = ((Organization) engagedParty).getOrganizationIdentification();
            for (OrganizationIdentification identification : CommonHelper.nullSafe(identifications)) {
                identification.setIdentificationId(cryptoService.decryptAndIgnoreError(identification.getIdentificationId()));
            }
        }
        LOGGER.debug("Decrypt party took {}ms", System.currentTimeMillis() - start);
    }

	/**
	 * Resolve BureauDecisionMessage from BureauDecisionCode
	 * 
	 * @param characteristic
	 */
	private void populateDecisionCode(TelusCreditProfileCharacteristic characteristic) {
        String decisionCode = characteristic.getBureauDecisionCode();
		if (StringUtils.isNotBlank(decisionCode)) {
            List<Key> keys = MultiKeyReferenceDataService.createKeyList(
                    PdsRefConstants.BUREAU_DECISION_CODE, decisionCode);
            MultiKeyReferenceDataItem data = referenceDataService.getCreditDecisionRule(keys);
            if (data == null)
            {
              characteristic.setBureauDecisionMessage(decisionCode);
              characteristic.setBureauDecisionMessage_fr(decisionCode);
      	      LOGGER.warn( "The bureau decision message not found in refpd data for " + decisionCode );
      		
            }
            else
            {
            data.getValues().forEach(value -> {
                if (PdsRefConstants.ENG_MESSAGE.equalsIgnoreCase(value.getValueCode())) {
                    characteristic.setBureauDecisionMessage(value.getValue());
                } else if (PdsRefConstants.FR_MESSAGE.equalsIgnoreCase(value.getValueCode())) {
                    characteristic.setBureauDecisionMessage_fr(value.getValue());
                	}
            	});
            }
        }
    }

    private void populateDecisionCode(TelusCreditProfile creditProfile) {
        String decisionCode = creditProfile.getBureauDecisionCd();
        if (StringUtils.isNotBlank(decisionCode)) {
            List<Key> keys = MultiKeyReferenceDataService.createKeyList(
                    PdsRefConstants.BUREAU_DECISION_CODE, decisionCode);
            MultiKeyReferenceDataItem data = referenceDataService.getCreditDecisionRule(keys);
            if (data == null)
            {
                creditProfile.setBureauDecisionCdTxtEn(decisionCode);
                creditProfile.setBureauDecisionCdTxtFr(decisionCode);
                LOGGER.warn( "The bureau decision message not found in refpd data for " + decisionCode );

            }
            else
            {
                data.getValues().forEach(value -> {
                    if (PdsRefConstants.ENG_MESSAGE.equalsIgnoreCase(value.getValueCode())) {
                        creditProfile.setBureauDecisionCdTxtEn(value.getValue());
                    } else if (PdsRefConstants.FR_MESSAGE.equalsIgnoreCase(value.getValueCode())) {
                        creditProfile.setBureauDecisionCdTxtFr(value.getValue());
                    }
                });
            }
        }
    }

	/**
	 * Resolve assessment message from AssessmentMessageCode
	 * 
	 * @param characteristic
	 */
	private void populateAssessmentMessage(TelusCreditProfileCharacteristic characteristic) {
        String messageCode = characteristic.getAssessmentMessageCode();
		if (StringUtils.isNotBlank(messageCode)) {
            List<Key> keys = MultiKeyReferenceDataService.createKeyList(
                    PdsRefConstants.MESSAGE_KEY, messageCode);
            MultiKeyReferenceDataItem data = referenceDataService.getAssessmentMessage(keys);
            
            if (data == null)
            		{
            	      characteristic.setAssessmentMessage(messageCode);
            	      characteristic.setAssessmentMessage_fr(messageCode);
            	      LOGGER.warn("The assessment message not found in refpd data for " + messageCode );
            		}
            else
            {
            try {
               
                data.getValues().forEach(value -> {
                    if (ASSESSMENT_MSG_VALUE_CODE.equals(value.getValueCode())) {
                        if (ApplicationConstants.EN_LANG.equalsIgnoreCase(value.getLangCode())) {
                            characteristic.setAssessmentMessage(value.getValue());
                        } if (ApplicationConstants.FR_LANG.equalsIgnoreCase(value.getLangCode())) {
                            characteristic.setAssessmentMessage_fr(value.getValue());
                        	}
                    	}
                	});
            	} catch (IllegalArgumentException e) {
            	LOGGER.warn(" Ignoring invalid key for assessment:{}",  e.getMessage());
            	}
            }
        }
    }

    private void populateAssessmentMessage(RiskLevelRiskAssessment riskLevelRiskAssessment) {
        LOGGER.info("start populateAssessmentMessage");
        if (ObjectUtils.isNotEmpty(riskLevelRiskAssessment)) {
            String messageCode = riskLevelRiskAssessment.getAssessmentMessageCd();
            if (StringUtils.isNotBlank(messageCode)) {
                List<Key> keys = MultiKeyReferenceDataService.createKeyList(
                        PdsRefConstants.MESSAGE_KEY, messageCode);
                MultiKeyReferenceDataItem data = referenceDataService.getAssessmentMessage(keys);

                if (data == null) {
                    riskLevelRiskAssessment.setAssessmentMessageTxtEn(messageCode);
                    riskLevelRiskAssessment.setAssessmentMessageTxtFr(messageCode);
                    LOGGER.warn("The assessment message not found in refpd data for " + messageCode);
                } else {
                    try {

                        data.getValues().forEach(value -> {
                            if (ASSESSMENT_MSG_VALUE_CODE.equals(value.getValueCode())) {
                                if (ApplicationConstants.EN_LANG.equalsIgnoreCase(value.getLangCode())) {
                                    riskLevelRiskAssessment.setAssessmentMessageTxtEn(value.getValue());
                                }
                                if (ApplicationConstants.FR_LANG.equalsIgnoreCase(value.getLangCode())) {
                                    riskLevelRiskAssessment.setAssessmentMessageTxtFr(value.getValue());
                                }
                            }
                        });
                    } catch (IllegalArgumentException e) {
                        LOGGER.warn(" Ignoring invalid key for assessment:{}", e.getMessage());
                    }
                }
            }
        }
    }
    
}