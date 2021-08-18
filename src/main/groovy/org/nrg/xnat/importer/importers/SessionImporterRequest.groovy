package org.nrg.xnat.importer.importers

import com.fasterxml.jackson.annotation.JsonProperty
import org.nrg.xnat.enums.MergeBehavior
import org.nrg.xnat.importer.XnatImportRequest
import org.nrg.xnat.importer.params.DestRequest
import org.nrg.xnat.importer.params.FileRequest
import org.nrg.xnat.importer.params.HttpSessionListenerRequest
import org.nrg.xnat.importer.params.ManualRoutingRequest
import org.nrg.xnat.importer.params.OverwriteRequest
import org.nrg.xnat.importer.params.QuarantineRequest
import org.nrg.xnat.importer.params.SessionRequest
import org.nrg.xnat.importer.params.TriggerPipelinesRequest

class SessionImporterRequest implements
        XnatImportRequest<SessionImporterRequest>,
        FileRequest<SessionImporterRequest>,
        HttpSessionListenerRequest<SessionImporterRequest>,
        DestRequest<SessionImporterRequest>,
        TriggerPipelinesRequest<SessionImporterRequest>,
        QuarantineRequest<SessionImporterRequest>,
        ManualRoutingRequest<SessionImporterRequest>,
        SessionRequest<SessionImporterRequest>,
        OverwriteRequest<SessionImporterRequest> {

    @JsonProperty('overwrite_files') Boolean overwriteFiles

    @Override
    String getImportHandler() {
        'SI'
    }

    @Override
    void performValidation() {
        verifyData()
    }

    SessionImporterRequest overwriteFiles(Boolean setting = true) {
        setOverwriteFiles(setting)
        this
    }

}
