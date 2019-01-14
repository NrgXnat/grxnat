package org.nrg.xnat.util.test

import org.nrg.xnat.util.TimeUtils
import org.testng.annotations.Test

import java.time.LocalTime
import java.time.temporal.ChronoField

import static org.testng.AssertJUnit.assertEquals

class TestTime {

    @Test
    void testDicomTMParsing() {
        final LocalTime standardTime = LocalTime.parse('071044', TimeUtils.DICOM_TM_FORMAT)
        assertEquals(7, standardTime.hour)
        assertEquals(10, standardTime.minute)
        assertEquals(44, standardTime.second)

        final LocalTime timeWithMilliseconds = LocalTime.parse('110810.754', TimeUtils.DICOM_TM_FORMAT)
        assertEquals(11, timeWithMilliseconds.hour)
        assertEquals(8, timeWithMilliseconds.minute)
        assertEquals(10, timeWithMilliseconds.second)
        assertEquals(754, timeWithMilliseconds.get(ChronoField.MILLI_OF_SECOND))

        final LocalTime timeWithMicroseconds = LocalTime.parse('155900.123456', TimeUtils.DICOM_TM_FORMAT)
        assertEquals(15, timeWithMicroseconds.hour)
        assertEquals(59, timeWithMicroseconds.minute)
        assertEquals(0, timeWithMicroseconds.second)
        assertEquals(123456, timeWithMicroseconds.get(ChronoField.MICRO_OF_SECOND))

        final LocalTime timeWithoutSeconds = LocalTime.parse('1319', TimeUtils.DICOM_TM_FORMAT)
        assertEquals(13, timeWithoutSeconds.hour)
        assertEquals(19, timeWithoutSeconds.minute)
        assertEquals(0, timeWithoutSeconds.second)

        final LocalTime timeWithoutMinutes = LocalTime.parse('13', TimeUtils.DICOM_TM_FORMAT)
        assertEquals(13, timeWithoutMinutes.hour)
        assertEquals(0, timeWithoutMinutes.minute)
        assertEquals(0, timeWithoutMinutes.second)

        println('Parsed times.')
    }

}
