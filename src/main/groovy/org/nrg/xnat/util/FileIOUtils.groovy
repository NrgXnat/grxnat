package org.nrg.xnat.util

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter

class FileIOUtils {

    static String readFile(File file) {
        try {
            FileUtils.readFileToString(file, "utf-8")
        } catch (IOException e) {
            throw new RuntimeException("Failed to read in file (${file}) due to: ${e}")
        }
    }

    static List<File> listFilesRecursively(File directory) {
        FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)
    }

}
