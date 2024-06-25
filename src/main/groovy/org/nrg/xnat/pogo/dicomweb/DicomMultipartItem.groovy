package org.nrg.xnat.pogo.dicomweb

import org.dcm4che3.data.Attributes

class DicomMultipartItem {

    String reportedTsUid
    Attributes dicom

    DicomMultipartItem reportedTsUid(String uid) {
        setReportedTsUid(uid)
        this
    }

    DicomMultipartItem dicom(Attributes dicom) {
        setDicom(dicom)
        this
    }

}
