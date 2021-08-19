package org.nrg.xnat.importer

import com.fasterxml.jackson.annotation.JsonInclude
import org.nrg.xnat.importer.params.DestRequest
import org.nrg.xnat.importer.params.OverwriteRequest
import org.nrg.xnat.importer.params.QuarantineRequest
import org.nrg.xnat.importer.params.SrcRequest
import org.nrg.xnat.importer.params.TriggerPipelinesRequest
import org.nrg.xnat.pogo.ArbitraryPropertySupport

@JsonInclude(JsonInclude.Include.NON_NULL)
class XnatArchivalRequest implements
        ArbitraryPropertySupport,
        QuarantineRequest<XnatArchivalRequest>,
        TriggerPipelinesRequest<XnatArchivalRequest>,
        DestRequest<XnatArchivalRequest>,
        SrcRequest<XnatArchivalRequest>,
        OverwriteRequest<XnatArchivalRequest> {

    XnatArchivalRequest param(String paramName, String paramValue) {
        add(paramName, paramValue)
        this
    }

}
