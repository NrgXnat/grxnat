package org.nrg.xnat.rest

interface XnatAuthProvider {

    XnatSessionFilter createSessionFilter(String xnatUrl, boolean allowInsecureSSL)

}