package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonProperty

class SearchColumnResponse {

    @JsonProperty('COLUMN') String columnName
    @JsonProperty('ID') String id
    @JsonProperty('Result') List<ColumnCount> result

}
