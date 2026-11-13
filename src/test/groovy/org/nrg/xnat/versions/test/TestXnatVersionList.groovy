package org.nrg.xnat.versions.test

import org.nrg.xnat.versions.*
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertEquals
import static org.testng.AssertJUnit.assertFalse
import static org.testng.AssertJUnit.assertTrue

class TestXnatVersionList {

    @Test
    void testXnatVersionLineage() {
        assertTrue(XnatVersionList.firstFollowsSecond(Xnat_1_7_3, Xnat_1_7_2))
        assertTrue(XnatVersionList.firstFollowsSecond(Xnat_1_7_3, Xnat_1_6dev))
        assertTrue(XnatVersionList.firstFollowsSecond(Xnat_1_7_2, Xnat_1_6dev))
        assertFalse(XnatVersionList.firstFollowsSecond(Xnat_1_7_3, Xnat_1_7_3))
        assertFalse(XnatVersionList.firstFollowsSecond(Xnat_1_7_2, Xnat_1_7_2))
        assertFalse(XnatVersionList.firstFollowsSecond(Xnat_1_7_2, Xnat_1_7_3))
        assertFalse(XnatVersionList.firstFollowsSecond(Xnat_1_6dev, Xnat_1_7_3))
    }

    @Test
    void test1_11_0FollowsAndResolves() {
        assertTrue(XnatVersionList.firstFollowsSecond(Xnat_1_11_0, Xnat_1_10_3))
        assertEquals(Xnat_1_11_0, XnatVersionList.lookup('1.11.0'))
        assertEquals(Xnat_1_11_0, XnatVersionList.lookup('1.11.0-SNAPSHOT'))
    }

    @Test
    void test1_10_2PatchesFollowAndResolve() {
        assertTrue(XnatVersionList.firstFollowsSecond(Xnat_1_10_2_1, Xnat_1_10_2))
        assertTrue(XnatVersionList.firstFollowsSecond(Xnat_1_10_2_2, Xnat_1_10_2_1))
        assertTrue(XnatVersionList.firstFollowsSecond(Xnat_1_10_3, Xnat_1_10_2_2))
        assertFalse(XnatVersionList.firstFollowsSecond(Xnat_1_10_2_1, Xnat_1_10_2_2))
        assertEquals(Xnat_1_10_2, XnatVersionList.lookup('1.10.2'))
        assertEquals(Xnat_1_10_2_1, XnatVersionList.lookup('1.10.2.1'))
        assertEquals(Xnat_1_10_2_2, XnatVersionList.lookup('1.10.2.2'))
    }

    @Test
    void testPartialMatchPrefersTheLongestKey() {
        assertEquals(Xnat_1_10_2, XnatVersionList.lookup('1.10.2-SNAPSHOT'))
        assertEquals(Xnat_1_10_2_1, XnatVersionList.lookup('1.10.2.1-SNAPSHOT'))
        assertEquals(Xnat_1_10_2_2, XnatVersionList.lookup('1.10.2.2-SNAPSHOT'))
        assertEquals(Xnat_1_8_10_2, XnatVersionList.lookup('1.8.10.2-SNAPSHOT'))
    }

}
