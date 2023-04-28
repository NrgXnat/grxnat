package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonIgnore

class SearchRow extends LinkedHashMap<String, String> {

    @JsonIgnore
    String getSessionId() {
        get('session_id')
    }

}
