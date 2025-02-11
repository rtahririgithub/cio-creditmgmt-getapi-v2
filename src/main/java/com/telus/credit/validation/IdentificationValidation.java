package com.telus.credit.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.CollectionUtils;

import com.telus.credit.model.RelatedPartyToPatch;

public class IdentificationValidation implements ConstraintValidator<ValidIdentification, RelatedPartyToPatch> {

    @Override
    public void initialize(ValidIdentification contactNumber) {
        // no need
    }

    @Override
    public boolean isValid(RelatedPartyToPatch value, ConstraintValidatorContext cxt) {
        if (value == null) {
            return true;
        }

        return !(!CollectionUtils.isEmpty(value.getIndividualIdentification())
                && !CollectionUtils.isEmpty(value.getOrganizationIdentification()));
    }

}