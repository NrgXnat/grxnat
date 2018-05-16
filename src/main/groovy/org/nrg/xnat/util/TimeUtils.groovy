package org.nrg.xnat.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

class TimeUtils {

    public static final DateTimeFormatter DICOM_DA_FORMAT = DateTimeFormatter.ofPattern('uuuuMMdd')
    public static final DateTimeFormatter DICOM_TM_FORMAT = tmFormatter()
    public static final DateTimeFormatter MM_DD_YYYY = DateTimeFormatter.ofPattern('MM/dd/uuuu')
    public static final DateTimeFormatter UNAMBIGUOUS_DATETIME = DateTimeFormatter.ofPattern('uuuu-MM-dd HH:mm:ss')

    static final LocalDate date(int year, int month, int day) {
        LocalDate.parse("${year}-${month}-${day}")
    }

    private static tmFormatter() {
        new DateTimeFormatterBuilder().appendPattern('HH').
                optionalStart().appendPattern('mm').
                optionalStart().appendPattern('ss').
                appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true).toFormatter()
    }

}
