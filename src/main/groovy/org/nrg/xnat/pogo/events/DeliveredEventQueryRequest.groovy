package org.nrg.xnat.pogo.events

import com.fasterxml.jackson.annotation.JsonProperty
import org.nrg.xnat.enums.PaginatedApiSortDirection

class DeliveredEventQueryRequest {

    int page = 1
    Integer size
    @JsonProperty('sort_col') DeliveredEventQuerySortColumn sortCol
    @JsonProperty('sort_dir') PaginatedApiSortDirection sortDir
    Map<DeliveredEventQueryFilterKey, DeliveredEventQueryFilter> filters = [:]

}
