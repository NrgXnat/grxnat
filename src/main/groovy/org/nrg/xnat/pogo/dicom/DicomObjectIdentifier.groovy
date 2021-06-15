package org.nrg.xnat.pogo.dicom

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class DicomObjectIdentifier {

    public static final DicomObjectIdentifier DEFAULT = new DicomObjectIdentifier('dicomObjectIdentifier', 'Default DICOM object identifier (ClassicDicomObjectIdentifier)')

    String id
    String displayName

    DicomObjectIdentifier(String id, String displayName) {
        setId(id)
        setDisplayName(displayName)
    }

    DicomObjectIdentifier() {}

}