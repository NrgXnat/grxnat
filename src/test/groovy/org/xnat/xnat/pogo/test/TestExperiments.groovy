package org.xnat.xnat.pogo.test

import org.apache.commons.lang3.StringUtils
import org.joda.time.LocalDate
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.experiments.sessions.MRSession
import org.testng.annotations.Test

import static org.testng.AssertJUnit.*

class TestExperiments {

    @Test
    void testImagingSession() {
        assertTrue(StringUtils.isNotEmpty(new ImagingSession().label))
    }

    @Test
    void testMRSession() {
        final LocalDate date = new LocalDate("2014-02-20")
        final Scan scan = new Scan().id("scan1")

        final MRSession mrSession = new MRSession().date(date)
        mrSession.addScan(scan)

        assertEquals(2014, mrSession.date.year)
        assertEquals([scan], mrSession.scans)
        assertEquals(DataType.MR_SESSION, mrSession.dataType)
    }

}
