package org.nrg.xnat.importer.params

import com.fasterxml.jackson.annotation.JsonProperty
import org.nrg.xnat.importer.XnatImportRequest

trait DirectArchiveRequest<X extends DirectArchiveRequest<X>> {

    @JsonProperty('Direct-Archive') Boolean directArchive

    X directArchive(Boolean setting = true) {
        setDirectArchive(setting)
        this as X
    }

}