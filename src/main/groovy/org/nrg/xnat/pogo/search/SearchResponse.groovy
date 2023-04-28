package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonProperty

class SearchResponse {

    @JsonProperty('Columns') List<Map<String, Object>> columns // could map this more precisely later if needed
    @JsonProperty('ID') String id
    @JsonProperty('Result') List<SearchRow> result
    String rootElementName
    int totalRecords

}
