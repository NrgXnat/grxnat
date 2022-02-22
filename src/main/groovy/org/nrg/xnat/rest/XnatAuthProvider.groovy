package org.nrg.xnat.rest

import com.fasterxml.jackson.databind.ObjectMapper

trait XnatAuthProvider {

    abstract XnatSessionFilter createSessionFilter(String xnatUrl, boolean allowInsecureSSL, ObjectMapper xnatRestMapper)

    boolean isAnonymous() {
        false
    }

}