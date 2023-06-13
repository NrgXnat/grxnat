package org.nrg.xnat.util

import org.apache.commons.lang3.RandomStringUtils

class RandomUtils {

    private static final int DEFAULT_ID_LENGTH = 10

    static String randomID(int length = DEFAULT_ID_LENGTH) {
        RandomStringUtils.randomAlphanumeric(length)
    }

}
