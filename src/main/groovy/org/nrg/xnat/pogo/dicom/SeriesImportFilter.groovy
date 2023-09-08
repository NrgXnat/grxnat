package org.nrg.xnat.pogo.dicom

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class SeriesImportFilter {

    boolean enabled
    FilterMode filterMode
    String filterBody

    // methods for Jackson with alternative property names
    void setMode(String mode) {
        setFilterMode(FilterMode.lookup(mode))
    }

    void setList(String body) {
        setFilterBody(body)
    }

}
