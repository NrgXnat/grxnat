package org.nrg.xnat.subinterfaces.test

import org.nrg.xnat.subinterfaces.AliasTokenSubinterface
import org.nrg.xnat.subinterfaces.AliasTokenSubinterface_1_7_4
import org.nrg.xnat.subinterfaces.XnatSubinterfaceVersionManager
import org.nrg.xnat.versions.Xnat_1_7_4
import org.nrg.xnat.versions.Xnat_1_7_5
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertEquals

class TestSubinterface {

    @Test
    void testSubinterfaceLookup() {
        assertEquals(AliasTokenSubinterface, XnatSubinterfaceVersionManager.lookupSubinterface(Xnat_1_7_5, AliasTokenSubinterface))
        assertEquals(AliasTokenSubinterface_1_7_4, XnatSubinterfaceVersionManager.lookupSubinterface(Xnat_1_7_4, AliasTokenSubinterface))
    }

}
