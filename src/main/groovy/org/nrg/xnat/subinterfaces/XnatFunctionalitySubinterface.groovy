package org.nrg.xnat.subinterfaces

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.collection.IsIn
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.rest.PermissionsException
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

    protected Matcher statusCodeValidatorWithPermissionsException(Integer... acceptableStatusCodes) {
        new IsIn<Integer>(acceptableStatusCodes) {
            @Override
            boolean matches(Object o) {
                if (o in [401, 403]) {
                    throw new PermissionsException("Request was blocked by XNAT with status code ${o}")
                }
                super.matches(o)
            }
        }
    }

    abstract List<String> getHandledEndpoints()

}
