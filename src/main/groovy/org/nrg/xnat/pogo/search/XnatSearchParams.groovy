package org.nrg.xnat.pogo.search

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class XnatSearchParams {

    int offset = 0
    int limit = 200
    String sortBy
    SortOrder sortOrder

    enum SortOrder {
        ASC, DESC
    }

}
