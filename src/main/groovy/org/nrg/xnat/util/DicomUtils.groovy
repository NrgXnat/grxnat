package org.nrg.xnat.util

class DicomUtils {

    static int stringHeaderToHexInt(String header) {
        Integer.parseInt(header.replaceAll("\\(|\\)|,| ", ""), 16)
    }

    static String intToSimpleHeaderString(int header) {
        String.format('%08X', header)
    }

}
