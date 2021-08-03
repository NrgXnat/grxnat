package org.nrg.xnat.importer.importers

import org.nrg.xnat.importer.XnatImportRequest
import org.nrg.xnat.importer.params.DestRequest
import org.nrg.xnat.importer.params.DirectArchiveRequest
import org.nrg.xnat.importer.params.FileRequest
import org.nrg.xnat.importer.params.ManualRoutingRequest
import org.nrg.xnat.importer.params.RenameRequest

class GradualDicomRequest implements
        XnatImportRequest<GradualDicomRequest>,
        DirectArchiveRequest<GradualDicomRequest>,
        FileRequest<GradualDicomRequest>,
        DestRequest<GradualDicomRequest>,
        RenameRequest<GradualDicomRequest>,
        ManualRoutingRequest<GradualDicomRequest> {

    @Override
    String getImportHandler() {
        'gradual-DICOM'
    }

    @Override
    void performValidation() {
        verifyData()
    }

}
