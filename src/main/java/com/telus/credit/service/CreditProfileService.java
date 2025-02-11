package com.telus.credit.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.telus.credit.common.RequestContext;
import com.telus.credit.model.CreditProfile;
import com.telus.credit.model.TelusAuditCharacteristic;
import com.telus.credit.model.TelusCreditProfile;

public interface CreditProfileService<T extends CreditProfile> {

    void createCreditProfile(String customerUid, TelusCreditProfile creditProfile, TelusAuditCharacteristic auditCharacteristic);

    void patchCreditProfile(String customerUid, TelusCreditProfile creditProfile, TelusAuditCharacteristic auditCharacteristic);

    List<T> getCreditProfiles(String customerUid);

    List<T> getCreditProfile(RequestContext context, String creditProfileId) throws ExecutionException, InterruptedException;
}
