package org.nrg.xnat.pogo

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter

trait ArbitraryPropertySupport {

    Map<String, Object> untrackedProperties = [:]

    @JsonAnyGetter
    Map<String, Object> getUntrackedProperties() {
        untrackedProperties
    }

    @JsonAnySetter
    void add(String key, Object value) {
        untrackedProperties.put(key, value);
    }

}