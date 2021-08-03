package org.nrg.xnat.importer

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
trait XnatImportRequest<X extends XnatImportRequest<X>> {

    @JsonProperty('import-handler') abstract String getImportHandler()
    Map<String, Object> additionalParams = [:]

    @JsonAnyGetter
    Map<String, Object> getAdditionalParams() {
        additionalParams
    }

    @JsonAnySetter
    void add(String key, Object value) {
        additionalParams.put(key, value);
    }

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
