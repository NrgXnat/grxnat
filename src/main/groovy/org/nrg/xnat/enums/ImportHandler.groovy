package org.nrg.xnat.enums

enum ImportHandler {

    SESSION_IMPORTER ('SI'),
    GRADUAL_DICOM ('gradual-DICOM'),
    DICOM_ZIP ('DICOM-zip'),
    XAR ('XAR'),
    DICOM_INBOX ('inbox')

    String handlerKey

    ImportHandler(String key) {
        handlerKey = key
    }

}