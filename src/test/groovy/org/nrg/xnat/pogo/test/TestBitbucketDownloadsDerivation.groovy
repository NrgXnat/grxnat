package org.nrg.xnat.pogo.test

import org.nrg.xnat.pogo.plugins.BitbucketDownloadsDerivation
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertFalse
import static org.testng.AssertJUnit.assertTrue

class TestBitbucketDownloadsDerivation {
    
    @Test
    void testArtifactMatching() {
        final BitbucketDownloadsDerivation bitbucketDownloadsDerivation = new BitbucketDownloadsDerivation()
        final String providedVersion = '1.0'
        [
                'containers-1.0.jar',
                'containers-1.0-fat.jar',
                'xnat-1.0.war'
        ].each { expectedMatch ->
            assertTrue(bitbucketDownloadsDerivation.artifactMatchesSuppliedVersion(expectedMatch, providedVersion))
        }
        [
                'containers-1.0.baz',
                'containers-1.1.0-fat.jar',
                'xnat-1.0-RC.war',
                'xnat-1.0-SNAPSHOT.war'
        ].each { expectedMatch ->
            assertFalse(bitbucketDownloadsDerivation.artifactMatchesSuppliedVersion(expectedMatch, providedVersion))
        }
    }
    
}
