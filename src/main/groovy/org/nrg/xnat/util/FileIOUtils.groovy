package org.nrg.xnat.util

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter

class FileIOUtils {

    static String readFile(File file) {
        file.text
    }

    static List<File> listFilesRecursively(File directory) {
        FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)
    }

}
