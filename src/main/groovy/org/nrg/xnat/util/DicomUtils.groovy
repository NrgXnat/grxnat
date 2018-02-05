package org.nrg.xnat.util

import org.dcm4che3.data.DatasetWithFMI
import org.dcm4che3.io.DicomInputStream
import org.dcm4che3.io.DicomOutputStream

import java.nio.file.Files

class DicomUtils {

    public static final int SMALLEST_DATASET_HEADER = 0x00080000

    static int stringHeaderToHexInt(String header) {
        Integer.parseInt(header.replaceAll("\\(|\\)|,| ", ""), 16)
    }

    static String intToSimpleHeaderString(int header) {
        String.format('%08X', header)
    }

    static DatasetWithFMI readDicom(File file) {
        readDicomFrom(file)
    }

    static DatasetWithFMI readDicom(InputStream inputStream) {
        readDicomFrom(inputStream)
    }

    static File writeDicomToFile(DatasetWithFMI fullDicom) {
        final File tempDicomFile = Files.createTempFile('dicom', '.dcm').toFile()
        writeDicomToFile(fullDicom, tempDicomFile)
        tempDicomFile
    }

    static void writeDicomToFile(DatasetWithFMI fullDicom, File file) {
        new DicomOutputStream(file).withCloseable { dicomStream ->
            dicomStream.writeDatasetWithFMI(fullDicom)
        }
    }

    private static DatasetWithFMI readDicomFrom(Object input) {
        // noinspection GroovyAssignabilityCheck
        new DicomInputStream(input).withCloseable { dicomStream ->
            dicomStream.readDatasetWithFMI()
        }
    }

}
