package org.nrg.xnat.rest

import com.jayway.restassured.filter.Filter
import com.jayway.restassured.filter.FilterContext
import com.jayway.restassured.internal.RequestSpecificationImpl
import com.jayway.restassured.response.Response
import com.jayway.restassured.specification.FilterableRequestSpecification
import com.jayway.restassured.specification.FilterableResponseSpecification
import com.jayway.restassured.specification.RequestSpecification
import org.apache.commons.lang3.time.StopWatch
import org.codehaus.groovy.runtime.ReflectionMethodInvoker
import org.nrg.testing.CommonStringUtils
import org.nrg.xnat.pogo.users.User

import java.util.concurrent.TimeUnit

class XnatSessionFilter implements Filter {

    private final String xnatUrl
    private final User user
    private String sessionId
    private final boolean allowInsecureSSL
    private final int[] authIssueCodes = [302, 401]
    private final int[] serverIssueCodes = [502, 503, 504]
    private int serverIssueRetryCount = 1
    private StopWatch stopWatch = new StopWatch()
    private static final int WAIT_TIME = 10000
    private static final int SESSION_TIMEOUT = 15 * 60 // Could pull and parse this from the site config. Not worth it for now

    XnatSessionFilter(User user, String xnatUrl, boolean allowInsecureSSL) {
        this.user = user
        this.xnatUrl = xnatUrl
        this.allowInsecureSSL = allowInsecureSSL
        stopWatch.start()
        extractSessionId()
    }

    @Override
    Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        if (stopWatch.getTime(TimeUnit.SECONDS) > SESSION_TIMEOUT || sessionId == null) {
            extractSessionId()
        }
        stopWatch.reset()
        stopWatch.start()

        final RequestSpecificationImpl request = preprocessRequest(requestSpec.noFiltersOfType(XnatSessionFilter))
        final Response response = issueRequest(request, ctx)

        if (response.statusCode in authIssueCodes || (response.statusCode == 200 && response.asString().contains('<!-- BEGIN xnat-templates/screens/Login.vm -->'))) {
            extractSessionId()
            return issueRequest(request.sessionId(sessionId), ctx)
        } else if (response.statusCode in serverIssueCodes) {
            println "Received an HTTP status code ${response.statusCode} from the XNAT server, indicating an issue with the server itself. The request will be repeated ${serverIssueRetryCount} more time${serverIssueRetryCount == 1 ? '' : 's'}."
            serverIssueRetryCount.times {
                final Response repeatedResponse = ctx.send(request)
                if (!(repeatedResponse.statusCode in serverIssueCodes)) {
                    return repeatedResponse
                } else {
                    sleep(WAIT_TIME)
                }
            }
        }
        response
    }

    String getXnatUrl() {
        xnatUrl
    }

    User getUser() {
        user
    }

    void deleteSessionId() {
        sessionId = null
    }

    String getSessionId() {
        sessionId
    }

    boolean getAllowInsecureSSL() {
        allowInsecureSSL
    }

    void setServerIssueRetryCount(int count) {
        serverIssueRetryCount = count
    }

    void extractSessionId() {
        final Response response = Credentials.build(user).get(CommonStringUtils.formatUrl(xnatUrl, '/data/auth'))
        if (response.statusCode == 200) {
            sessionId = response.getSessionId()
        } else {
            throw new RuntimeException('Provided username and password combination do not appear to be correct')
        }
    }

    private RequestSpecificationImpl preprocessRequest(RequestSpecification requestSpec) {
        ((allowInsecureSSL) ? requestSpec.relaxedHTTPSValidation() : requestSpec).sessionId(sessionId) as RequestSpecificationImpl
    }

    private Response issueRequest(RequestSpecification request, FilterContext ctx) {
        ReflectionMethodInvoker.invoke(request, ctx.requestMethod.toString().toLowerCase(), URLDecoder.decode(ctx.requestPath, 'UTF-8')) as Response// TODO: this is a hack copied out of FilterContextImpl. It will likely need to be modified or thrown out when upgrading RestAssured
    }

}
