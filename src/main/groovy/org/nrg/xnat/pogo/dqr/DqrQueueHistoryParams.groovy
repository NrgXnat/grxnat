package org.nrg.xnat.pogo.dqr

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.nrg.xnat.pogo.search.SortOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(builderStrategy = SimpleStrategy, prefix = '')
class DqrQueueHistoryParams {

    Boolean all
    Integer page
    Integer pageSize
    SortOrder sort

}
