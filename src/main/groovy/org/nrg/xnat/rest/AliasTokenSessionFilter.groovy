package org.nrg.xnat.rest

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.filter.FilterContext
import io.restassured.internal.RequestSpecificationImpl
import io.restassured.response.Response
import io.restassured.specification.FilterableRequestSpecification
import io.restassured.specification.FilterableResponseSpecification
import io.restassured.specification.RequestSpecification

class AliasTokenSessionFilter extends XnatSessionFilter {

    private XnatAliasToken aliasToken

    AliasTokenSessionFilter(XnatAliasToken aliasToken, String xnatUrl, boolean allowInsecureSSL, ObjectMapper xnatRestMapper) {
        super(null, xnatUrl, allowInsecureSSL, xnatRestMapper)
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
