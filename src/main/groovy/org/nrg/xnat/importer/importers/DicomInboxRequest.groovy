package org.nrg.xnat.importer.importers

import org.nrg.xnat.importer.XnatImportRequest
import org.nrg.xnat.importer.params.ManualRoutingRequest
import org.nrg.xnat.importer.params.QuarantineRequest
import org.nrg.xnat.importer.params.RenameRequest
import org.nrg.xnat.importer.params.SessionRequest
import org.nrg.xnat.importer.params.TriggerPipelinesRequest

class DicomInboxRequest implements
        XnatImportRequest<DicomInboxRequest>,
        TriggerPipelinesRequest<DicomInboxRequest>,
        RenameRequest<DicomInboxRequest>,
        QuarantineRequest<DicomInboxRequest>,
        ManualRoutingRequest<DicomInboxRequest>,
        SessionRequest<DicomInboxRequest> {

    Boolean cleanupAfterImport
    String path

    @Override
    String getImportHandler() {
        'inbox'
    }

    @Override
    void performValidation() {
        checkParam('projectId', projectId)
        checkParam('path', path)
    }

    DicomInboxRequest cleanupAfterImport(Boolean setting = true) {
        setCleanupAfterImport(setting)
        this
    }

    DicomInboxRequest path(String path) {
        setPath(path)
        this
    }

}
