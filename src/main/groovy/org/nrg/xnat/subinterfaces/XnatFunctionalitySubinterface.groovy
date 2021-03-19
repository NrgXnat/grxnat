package org.nrg.xnat.subinterfaces

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.versions.XnatVersion

abstract class XnatFunctionalitySubinterface {

    @Delegate(includes = [
            'queryBase',
            'jsonQuery',
            'formatRestUrl',
            'formatXapiUrl',
            'formatXnatUrl'
    ])
    XnatInterface xnatInterface

    List<Class<? extends XnatVersion>> supportedVersions() {
        []
    }

    protected void notSupported() {
        throw new UnsupportedOperationException('REST call not supported in this version of XNAT.')
    }

    protected void prohibitNonadmin() {
        if (!xnatInterface.userIsAdmin()) {
            throw new UnsupportedOperationException('You must be an admin to perform this operation.')
        }
    }

    abstract List<String> getHandledEndpoints()

}
