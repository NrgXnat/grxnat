package org.nrg.xnat.rest

trait XnatAuthProvider {

    abstract XnatSessionFilter createSessionFilter(String xnatUrl, boolean allowInsecureSSL)

    boolean isAnonymous() {
        false
    }

}