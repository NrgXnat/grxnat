package org.nrg.xnat.importer

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.nrg.xnat.pogo.ArbitraryPropertySupport

@JsonInclude(JsonInclude.Include.NON_NULL)
trait XnatImportRequest<X extends XnatImportRequest<X>> implements ArbitraryPropertySupport {

    @JsonProperty('import-handler') abstract String getImportHandler()

    abstract void performValidation()

    X param(String name, Object value) {
        add(name, value)
        this as X
    }

    X validate() {
        performValidation()
        this as X
    }

    void checkParam(String paramName, Object paramValue) {
        if (paramValue == null) {
            throw new UnsupportedOperationException("Missing required parameter: ${paramName}")
        }
    }

}
