package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonProperty

class SearchResponse {

    @JsonProperty('Columns') List<SearchColumn> columns
    @JsonProperty('ID') String id
    @JsonProperty('Result') List<SearchRow> result
    String rootElementName
    int totalRecords

}
