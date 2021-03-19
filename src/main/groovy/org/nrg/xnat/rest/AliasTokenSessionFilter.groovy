package org.nrg.xnat.rest

import com.jayway.restassured.filter.FilterContext
import com.jayway.restassured.internal.RequestSpecificationImpl
import com.jayway.restassured.response.Response
import com.jayway.restassured.specification.FilterableRequestSpecification
import com.jayway.restassured.specification.FilterableResponseSpecification
import com.jayway.restassured.specification.RequestSpecification

class AliasTokenSessionFilter extends XnatSessionFilter {

    private XnatAliasToken aliasToken

    AliasTokenSessionFilter(XnatAliasToken aliasToken, String xnatUrl, boolean allowInsecureSSL) {
        super(null, xnatUrl, allowInsecureSSL)
        this.aliasToken = aliasToken
    }

    @Override
    Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        final RequestSpecificationImpl request = preprocessRequest(requestSpec.noFiltersOfType(AliasTokenSessionFilter)).auth().preemptive().basic(aliasToken.alias, aliasToken.secret) as RequestSpecificationImpl
        issueRequest(request, ctx)
    }

    @Override
    void extractSessionId() {}

    @Override
    protected RequestSpecificationImpl preprocessRequest(RequestSpecification requestSpec) {
        ((allowInsecureSSL) ? requestSpec.relaxedHTTPSValidation() : requestSpec) as RequestSpecificationImpl
    }

}
