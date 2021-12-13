package org.nrg.xnat.rest

class AnonymousAuth implements XnatAuthProvider {

    @Override
    XnatSessionFilter createSessionFilter(String xnatUrl, boolean allowInsecureSSL) {
        null
    }

    @Override
    boolean isAnonymous() {
        true
    }

}
