package org.nrg.xnat.importer.importers

import org.nrg.xnat.importer.XnatImportRequest
import org.nrg.xnat.importer.params.FileRequest
import org.nrg.xnat.importer.params.HttpSessionListenerRequest

class XARRequest implements XnatImportRequest<XARRequest>, FileRequest<XARRequest>, HttpSessionListenerRequest<XARRequest> {

    @Override
    String getImportHandler() {
        'SI'
    }

    @Override
    void performValidation() {
        verifyData()
    }

}
