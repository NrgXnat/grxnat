package org.nrg.xnat.rest

import com.jayway.restassured.filter.Filter
import com.jayway.restassured.filter.FilterContext
import com.jayway.restassured.response.Response
import com.jayway.restassured.specification.FilterableRequestSpecification
import com.jayway.restassured.specification.FilterableResponseSpecification
import com.jayway.restassured.specification.RequestSpecification
import org.apache.http.impl.client.SystemDefaultHttpClient
import org.nrg.testing.CommonUtils
import org.nrg.xnat.pogo.users.User

class XnatSessionFilter implements Filter {

    private final String xnatUrl
    private final User user
    private String sessionId
    private final boolean allowInsecureSSL
    private final int[] authIssueCodes = [302, 401]
    private final int[] serverIssueCodes = [502, 503, 504]
    private final int waitTime = 10000
    private int serverIssueRetryCount = 1

    XnatSessionFilter(User user, String xnatUrl, boolean allowInsecureSSL) {
        this.user = user
        this.xnatUrl = xnatUrl
        this.allowInsecureSSL = allowInsecureSSL
        extractSessionId()
    }

    @SuppressWarnings(["ChangeToOperator", "GrDeprecatedAPIUsage"])
    @Override
    Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        final FilterableRequestSpecification request = (allowInsecureSSL) ? requestSpec.relaxedHTTPSValidation() : requestSpec

        if (sessionId != null) request = request.sessionId(sessionId)
        final Response response = request.sendRequest(ctx.getRequestPath(), ctx.getRequestMethod(), ctx.assertionClosure, request)

        if (response.statusCode in authIssueCodes || (response.statusCode == 200 && response.asString().contains('<!-- BEGIN xnat-templates/screens/Login.vm -->'))) {
            extractSessionId()
            resetClient(request)
            request.sessionId(sessionId).sendRequest(ctx.getRequestPath(), ctx.getRequestMethod(), ctx.assertionClosure, request)
        } else if (response.statusCode in serverIssueCodes) {
            println "Received an HTTP status code ${response.statusCode} from the XNAT server, indicating an issue with the server itself. The request will be repeated ${serverIssueRetryCount} more time${serverIssueRetryCount == 1 ? '' : 's'}."
            for (int i = 0; i < serverIssueRetryCount; i++) {
                resetClient(request)
                final Response repeatedResponse = request.sendRequest(ctx.getRequestPath(), ctx.getRequestMethod(), ctx.assertionClosure, request)
                if (!(repeatedResponse.statusCode in serverIssueCodes)) {
                    return repeatedResponse
                } else {
                    sleep(waitTime)
                }
            }
        }
        response
    }

    String getXnatUrl() {
        return xnatUrl
    }

    User getUser() {
        return user
    }

    String getSessionId() {
        return sessionId
    }

    boolean getAllowInsecureSSL() {
        return allowInsecureSSL
    }

    void setServerIssueRetryCount(int count) {
        serverIssueRetryCount = count
    }

    void extractSessionId() {
        final Response response = Credentials.build(user).get(CommonUtils.formatUrl(xnatUrl, '/data/auth'))
        if (response.statusCode != 200) throw new AssertionError('Provided username and password combination do not appear to be correct')
        sessionId = response.getSessionId()
    }

    @SuppressWarnings('GrDeprecatedAPIUsage')
    private void resetClient(RequestSpecification request) {
        request.httpClient.connectionManager.shutdown()
        request.httpClient = new SystemDefaultHttpClient()
    }

}
