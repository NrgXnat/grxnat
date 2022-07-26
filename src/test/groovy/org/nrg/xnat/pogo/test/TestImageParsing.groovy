package org.nrg.xnat.pogo.test

import org.nrg.xnat.pogo.containers.Image
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertEquals

class TestImageParsing {

    private static final String USER = 'xnat'
    private static final String IMAGE_NAME = 'debug-command'
    private static final String VERSION = 'latest'
    private static final String HOST = 'gcr.io'

    @Test
    void testImageParsingWithoutHost() {
        final Image image = new Image()
        image.setNames(["${USER}/${IMAGE_NAME}:${VERSION}"])
        assertEquals(USER, image.user)
        assertEquals(IMAGE_NAME, image.name)
        assertEquals(VERSION, image.version)
    }

    @Test
    void testImageParsingWithHost() {
        final Image image = new Image()
        image.setNames(["${HOST}/${USER}/${IMAGE_NAME}:${VERSION}"])
        assertEquals("${HOST}/${USER}", image.user)
        assertEquals(IMAGE_NAME, image.name)
        assertEquals(VERSION, image.version)
    }

}
