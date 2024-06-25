package org.nrg.xnat.rest

import io.restassured.filter.FilterContext
import io.restassured.internal.RequestSpecificationImpl
import io.restassured.response.Response
import io.restassured.specification.FilterableRequestSpecification
import io.restassured.specification.FilterableResponseSpecification
import io.restassured.specification.RequestSpecification
import org.nrg.xnat.pogo.users.User

class AnonymousSessionFilter extends XnatSessionFilter {

    private static final CustomExceptionFailureListener UNAUTHORIZED_401_LISTENER = new UnauthorizedListener()

    AnonymousSessionFilter(User user, String xnatUrl, boolean allowInsecureSSL) {
        super(null, null, allowInsecureSSL)
    }

    AnonymousSessionFilter(boolean allowInsecureSSL) {
        this(null, null, allowInsecureSSL)
    }

    @Override
    Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        requestSpec.config.failureConfig.failureListeners << UNAUTHORIZED_401_LISTENER
        issueRequest(preprocessRequest(requestSpec.noFiltersOfType(AnonymousSessionFilter)), ctx)
    }

    @Override
    protected RequestSpecificationImpl preprocessRequest(RequestSpecification requestSpec) {
        ((allowInsecureSSL) ? requestSpec.relaxedHTTPSValidation() : requestSpec) as RequestSpecificationImpl
    }

    @Override
    void extractSessionId() {

    }

}
