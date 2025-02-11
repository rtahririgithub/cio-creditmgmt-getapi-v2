package com.telus.credit.common;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class DateTimeUtils {

    private DateTimeUtils() {
        // Utils
    }

    public static Date toUtcDate(String isoDate) {
        if (StringUtils.isBlank(isoDate)) {
            return null;
        }

        return Date.from(LocalDate.from(DateTimeFormatter.ISO_DATE.parse(isoDate)).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Timestamp toUtcTimestamp(String isoDatetime) {
        if (StringUtils.isBlank(isoDatetime)) {
            return null;
        }
        Instant instant = Instant.parse(isoDatetime);
        return new Timestamp(instant.minusSeconds(ZoneId.systemDefault().getRules().getOffset(instant).getTotalSeconds()).toEpochMilli());
    }
}
