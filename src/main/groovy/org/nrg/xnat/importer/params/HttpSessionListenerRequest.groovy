package org.nrg.xnat.importer.params

import com.fasterxml.jackson.annotation.JsonProperty

trait HttpSessionListenerRequest<X extends HttpSessionListenerRequest<X>> {

    @JsonProperty('http-session-listener') String httpSessionListener

    X httpSessionListener(String listener) {
        setHttpSessionListener(listener)
        this as X
    }

}