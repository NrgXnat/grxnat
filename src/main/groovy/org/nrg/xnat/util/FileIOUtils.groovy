package org.nrg.xnat.util

import org.apache.commons.io.FileUtils

class FileIOUtils {

    static String readFile(File file) {
        try {
            FileUtils.readFileToString(file, "utf-8")
        } catch (IOException e) {
            throw new RuntimeException("Failed to read in file (${file}) due to: ${e}")
        }
    }

}
