package com.telus.credit.validation;

import java.time.format.DateTimeParseException;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telus.credit.common.DateTimeUtils;
import com.telus.credit.model.TimePeriod;

public class TimePeriodValidation implements ConstraintValidator<ValidTimePeriod, TimePeriod> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimePeriodValidation.class);

    @Override
    public void initialize(ValidTimePeriod contactNumber) {
        // no need
    }

    @Override
    public boolean isValid(TimePeriod value, ConstraintValidatorContext cxt) {
        if (value == null) {
            return true;
        }

        if (Stream.of(value.getStartDateTime(), value.getEndDateTime()).anyMatch(StringUtils::isBlank)) {
            return true;
        }

        try {
            return DateTimeUtils.toUtcTimestamp(value.getStartDateTime())
                    .before(DateTimeUtils.toUtcTimestamp(value.getEndDateTime()));
        } catch (DateTimeParseException e) {
            LOGGER.warn("Invalid input {}", e.getMessage());
            // return true in case we cannot handle (let the other handle)
            return true;
        }
    }

}