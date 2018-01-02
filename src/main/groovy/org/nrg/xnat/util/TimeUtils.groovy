package org.nrg.xnat.util

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

class TimeUtils {

    public static final DateTimeFormatter DICOM_DA_FORMAT = DateTimeFormatter.ofPattern('YYYYMMdd')
    public static final DateTimeFormatter DICOM_TM_FORMAT = tmFormatter()

    private static tmFormatter() {
        new DateTimeFormatterBuilder().appendPattern('HH').
                optionalStart().appendPattern('mm').
                optionalStart().appendPattern('ss').
                appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true).toFormatter()
    }

}
