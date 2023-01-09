package org.nrg.xnat.pogo

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.nrg.xnat.rest.SerializationUtils

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class ArchiveParams {

    Overwrite overwrite

    ArchiveParams append() {
        overwrite(Overwrite.APPEND)
    }

    ArchiveParams delete() {
        overwrite(Overwrite.DELETE)
    }

    Map<String, Object> formParams(String src) {
        final Map<String, Object> asMap = SerializationUtils.serializeToMap(this).findAll { it.value != null }
        asMap.put('src', src)
        asMap
    }

    enum Overwrite {
        APPEND,
        DELETE
    }

}
