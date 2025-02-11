package com.telus.credit.utils;

import com.telus.credit.model.CreditProfile;
import com.telus.credit.model.Customer;
import com.telus.credit.model.Individual;
import com.telus.credit.model.RelatedParty;
import com.telus.credit.model.TelusCreditProfile;
import com.telus.credit.model.TelusIndividualIdentification;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public interface CreditProfileUtil {

    static CreditProfile getPrimaryCreditProfile(Customer customer) {
        if (Objects.isNull(customer)) {
            return null;
        }
        List<TelusCreditProfile> creditProfiles = customer.getCreditProfile();
        if (CollectionUtils.isEmpty(creditProfiles)) {
            return null;
        }
        Optional<TelusCreditProfile> primaryProfileOptional = creditProfiles.stream().filter(creditProfile -> "PRI".equalsIgnoreCase(creditProfile.getCustomerCreditProfileRelCd())).findFirst();
        return primaryProfileOptional.orElse(null);
    }

    static List<RelatedParty> getRelatedPartiesWithCustomerRole(CreditProfile creditProfile) {
        List<RelatedParty> relatedParties = new LinkedList<>();
        if (Objects.isNull(creditProfile)) {
            return relatedParties;
        }
        List<RelatedParty> parties = creditProfile.getRelatedParty();
        if (CollectionUtils.isEmpty(parties)) {
            return relatedParties;
        }
        return parties.stream().filter(party -> "customer".equalsIgnoreCase(party.getRole())).collect(Collectors.toList());
    }

    static Map<String, String> getIdentifications(RelatedParty party) {
        Map<String, String> identificationMap = new HashMap<>();
        if (Objects.isNull(party)) {
            return identificationMap;
        }
        Individual individual = party.getIndividual();
        if (Objects.isNull(individual)) {
            return identificationMap;
        }
        List<TelusIndividualIdentification> identifications = individual.getIndividualIdentification();
        if (CollectionUtils.isEmpty(identifications)) {
            return identificationMap;
        }
        return identifications.stream().collect(Collectors.toMap(
                TelusIndividualIdentification::getIdentificationType, TelusIndividualIdentification::getIdentificationId
        ));
    }
}
