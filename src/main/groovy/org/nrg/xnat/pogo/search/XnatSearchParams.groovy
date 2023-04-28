package org.nrg.xnat.pogo.search

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class XnatSearchParams {

    Integer offset
    Integer limit
    String sortBy
    SortOrder sortOrder
    Boolean cache
    Boolean refresh

    enum SortOrder {
        ASC, DESC
    }

}
