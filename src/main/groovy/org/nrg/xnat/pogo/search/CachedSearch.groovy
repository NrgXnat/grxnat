package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonProperty

class CachedSearch {

    @JsonProperty('Columns') List<Map<String, Object>> columns // could map this more precisely later if needed
    @JsonProperty('ID') String id
    String rootElementName
    int totalRecords

}
