package org.nrg.xnat.jackson

import org.nrg.xnat.versions.XnatVersion
import org.nrg.xnat.versions.Xnat_1_7_3
import org.nrg.xnat.versions.Xnat_1_7_5
import org.nrg.xnat.versions.Xnat_1_8_0
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertEquals

class TestSerializationProvider {

    @Test
    void testSerializerProviderLookup() {
        assertEquals(BogusSerializer, XnatSerializationProviderManager.getProviderFor(BogusSerializer, Xnat_1_8_0))
        assertEquals(BogusSerializer, XnatSerializationProviderManager.getProviderFor(BogusSerializer, Xnat_1_7_3))
        assertEquals(BogusSerializer_1_7_5, XnatSerializationProviderManager.getProviderFor(BogusSerializer, Xnat_1_7_5))
    }

    class BogusObject {}

    class BogusSerializer implements XnatSerializationProvider {}

    class BogusSerializer_1_7_5 extends BogusSerializer {
        @Override
        List<Class<? extends XnatVersion>> getHandledVersions() {
            [Xnat_1_7_5]
        }
    }

}
