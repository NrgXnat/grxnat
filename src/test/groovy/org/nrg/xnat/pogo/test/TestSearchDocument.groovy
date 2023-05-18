package org.nrg.xnat.pogo.test

import org.nrg.testing.FileIOUtils
import org.nrg.xnat.pogo.search.XnatSearchDocument
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertEquals

class TestSearchDocument {

    final File sampleXmlOriginal = FileIOUtils.loadResource('sample_search_original.xml')
    final File sampleXmlExpectation = FileIOUtils.loadResource('sample_search_expected_post_process.xml')

    @Test
    void testRoundTrip() {
        assertEquals(
                sampleXmlExpectation.text,
                XnatSearchDocument.fromXml(sampleXmlOriginal).asString()
        )
    }

}
