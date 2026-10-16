package org.nrg.xnat.pogo.test

import org.nrg.xnat.pogo.XnatDeployment
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertEquals

class TestXnatDeployment {

    @Test
    void testImageTagDefaultsToTheXnatVersion() {
        assertEquals('1.10.1', new XnatDeployment(xnatVersionString: '1.10.1').imageTag)
    }

}
