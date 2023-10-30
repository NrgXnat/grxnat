package org.nrg.xnat.importer.importers

import com.fasterxml.jackson.annotation.JsonProperty
import org.nrg.xnat.importer.XnatImportRequest
import org.nrg.xnat.importer.params.DestRequest
import org.nrg.xnat.importer.params.DirectArchiveRequest
import org.nrg.xnat.importer.params.FileRequest
import org.nrg.xnat.importer.params.ManualRoutingRequest
import org.nrg.xnat.importer.params.OverwriteRequest
import org.nrg.xnat.importer.params.RenameRequest

class DicomZipRequest implements
        XnatImportRequest<DicomZipRequest>,
        DirectArchiveRequest<DicomZipRequest>,
        FileRequest<DicomZipRequest>,
        DestRequest<DicomZipRequest>,
        RenameRequest<DicomZipRequest>,
        ManualRoutingRequest<DicomZipRequest>,
        OverwriteRequest<DicomZipRequest> {

    @JsonProperty('Ignore-Unparsable') Boolean ignoreUnparsable

    @Override
    String getImportHandler() {
        'DICOM-zip'
    }

    @Override
    void performValidation() {
        verifyData()
    }

    DicomZipRequest ignoreUnparsable(Boolean setting = true) {
        setIgnoreUnparsable(setting)
        this
    }

}
