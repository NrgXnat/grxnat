package org.nrg.xnat.subinterfaces

import io.restassured.RestAssured
import io.restassured.config.FailureConfig
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matcher
import org.hamcrest.collection.IsIn
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.rest.CustomExceptionFailureListener
import org.nrg.xnat.rest.ForbiddenListener
import org.nrg.xnat.rest.NotFoundListener
import org.nrg.xnat.rest.UnprocessableContentListener
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

    public static final CustomExceptionFailureListener FORBIDDEN_403 = new ForbiddenListener()
    public static final CustomExceptionFailureListener NOT_FOUND_404 = new NotFoundListener()
    public static final CustomExceptionFailureListener UNPROCESSABLE_CONTENT_422 = new UnprocessableContentListener()

    protected void notSupported() {
        throw new UnsupportedOperationException('REST call not supported in this version of XNAT.')
    }

    protected void prohibitNonadmin() {
        if (!xnatInterface.userIsAdmin()) {
            throw new UnsupportedOperationException('You must be an admin to perform this operation.')
        }
    }

    protected RequestSpecification queryBaseWithStatusCodeListeners(List<CustomExceptionFailureListener> listeners) {
        addStatusCodeListeners(queryBase(), listeners)
    }

    protected RequestSpecification jsonQueryWithStatusCodeListeners(List<CustomExceptionFailureListener> listeners) {
        addStatusCodeListeners(jsonQuery(), listeners)
    }

    protected RequestSpecification queryBaseWithCommonStatusCodeListeners() {
        queryBaseWithStatusCodeListeners([FORBIDDEN_403, NOT_FOUND_404])
    }

    @SuppressWarnings('GrMethodMayBeStatic')
    protected RequestSpecification addStatusCodeListeners(RequestSpecification requestSpecification, List<CustomExceptionFailureListener> listeners) {
        requestSpecification.config(RestAssured.config().failureConfig(FailureConfig.failureConfig().with().failureListeners(listeners)))
    }

    abstract List<String> getHandledEndpoints()

}
