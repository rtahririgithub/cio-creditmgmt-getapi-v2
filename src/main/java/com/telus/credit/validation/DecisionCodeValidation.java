package com.telus.credit.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.telus.credit.common.PdsRefConstants;
import com.telus.credit.pds.model.Key;
import com.telus.credit.pds.service.MultiKeyReferenceDataService;
import com.telus.credit.pds.service.ReferenceDataService;

@Component
public class DecisionCodeValidation implements ConstraintValidator<ValidDecisionCode, String> {

    private static ReferenceDataService referenceDataService;

    @Autowired
    public void setReferenceDataService(ReferenceDataService referenceDataService) {
        DecisionCodeValidation.referenceDataService = referenceDataService;
    }

    @Override
    public void initialize(ValidDecisionCode annotation) {
        // no need
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext cxt) {
        if (StringUtils.isBlank(value)) {
            return true;
        }

        try {
            List<Key> keys = MultiKeyReferenceDataService.createKeyList(PdsRefConstants.BUREAU_DECISION_CODE, value);
            referenceDataService.getCreditDecisionRule(keys);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

}