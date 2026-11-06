package org.nrg.xnat

import org.nrg.xnat.versions.XnatVersion

class XnatConnectionConfig {

    boolean allowInsecureSSL = false
    Class<? extends XnatVersion> versionClass
    boolean skipAuth = false
    boolean logOnValidationFailure = true
    /**
     * Fail a request when XNAT rejects its session, instead of logging in again. See {@link org.nrg.xnat.rest.XnatSessionFilter#setStrictSessions(boolean)}.
     */
    boolean strictSessions = false

}
