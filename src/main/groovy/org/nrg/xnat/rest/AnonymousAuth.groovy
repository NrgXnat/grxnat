package org.nrg.xnat.rest

import com.fasterxml.jackson.databind.ObjectMapper

class AnonymousAuth implements XnatAuthProvider {

    @Override
    XnatSessionFilter createSessionFilter(String xnatUrl, boolean allowInsecureSSL, ObjectMapper xnatRestMapper) {
        null
    }

    @Override
    boolean isAnonymous() {
        true
    }

}
