package org.nrg.xnat.util.test

import org.dcm4che3.data.Tag
import org.nrg.xnat.util.DicomUtils
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertEquals

class TestDicomUtils {

    @Test
    void testIntToString() {
        assertEquals('0020000D', DicomUtils.intToSimpleHeaderString(Tag.StudyInstanceUID))
        assertEquals('00081030', DicomUtils.intToSimpleHeaderString(Tag.StudyDescription))
    }

    @Test
    void testStringToInt() {
        assertEquals(Tag.StudyInstanceUID, DicomUtils.stringHeaderToHexInt('(0020,000d)'))
        assertEquals(Tag.SeriesInstanceUID, DicomUtils.stringHeaderToHexInt('(0020,000E)'))
        assertEquals(Tag.StudyDescription, DicomUtils.stringHeaderToHexInt('00081030'))
    }

}
