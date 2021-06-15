package org.nrg.xnat

import org.nrg.xnat.versions.XnatVersion

class XnatConnectionConfig {

    boolean allowInsecureSSL = false
    Class<? extends XnatVersion> versionClass
    boolean skipAuth = false
    boolean logOnValidationFailure = true

}
