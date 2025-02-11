package com.telus.credit.service;

import com.telus.credit.model.RelatedPartyInterface;
import com.telus.credit.model.RelatedPartyToPatch;
import com.telus.credit.model.TelusAuditCharacteristic;

public interface EngagedPartyService {

    String createEngagedParty(RelatedPartyToPatch engagedParty, TelusAuditCharacteristic auditCharacteristic);

    void patchEngagedParty(String id, RelatedPartyToPatch engagedParty, TelusAuditCharacteristic auditCharacteristic);

    RelatedPartyInterface getEngagedParty(String partyId);
}
