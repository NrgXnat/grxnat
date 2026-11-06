package org.nrg.xnat.rest

import io.restassured.filter.Filter
import io.restassured.filter.FilterContext
import io.restassured.internal.RequestSpecificationImpl
import io.restassured.internal.ValidatableResponseImpl
import io.restassured.internal.filter.FilterContextImpl
import io.restassured.response.Response
import io.restassured.specification.FilterableRequestSpecification
import io.restassured.specification.FilterableResponseSpecification
import io.restassured.specification.RequestSpecification
import org.apache.commons.lang3.time.StopWatch
import org.codehaus.groovy.runtime.ReflectionMethodInvoker
import org.nrg.testing.CommonStringUtils
import org.nrg.xnat.pogo.users.User

import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class XnatSessionFilter implements Filter {

    private final String xnatUrl
    private final User user
    private String sessionId
    private final boolean allowInsecureSSL
    private final int[] authIssueCodes = [302, 401]
    private final int[] serverIssueCodes = [502, 503, 504]
    private int serverIssueRetryCount = 1
    private boolean strictSessions = false
    private StopWatch stopWatch = new StopWatch()
    private static final int WAIT_TIME = 10000
    private static final int SESSION_TIMEOUT = 15 * 60 // Could pull and parse this from the site config. Not worth it for now
    private static final int[] REDIRECT_CODES = [301, 302, 303, 307, 308]
    // The comment Login.vm opens with, e.g. <!-- BEGIN xnat-templates/screens/Login.vm -->. A bare 'Login.vm' isn't enough:
    // ordinary pages link to the login page, and some send the browser there from JavaScript when a call returns 401.
    private static final Pattern LOGIN_PAGE_MARKER = Pattern.compile('<!--\\s*BEGIN\\s+(?:\\S*/)?screens/Login\\.vm\\s*-->')
    private static final Pattern URL_SCHEME_AND_AUTHORITY = Pattern.compile('^(?:[a-zA-Z][a-zA-Z0-9+.-]*:)?//[^/]*')

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

        if (strictSessions) {
            failIfSessionRejected(requestSpec, response)
        } else if (response.statusCode in authIssueCodes || (response.statusCode == 200 && response.asString().contains('<!-- BEGIN xnat-templates/screens/Login.vm -->'))) {
            extractSessionId()
            return issueRequest(request.sessionId(sessionId), ctx)
        }
        if (response.statusCode in serverIssueCodes) {
            println "Received an HTTP status code ${response.statusCode} from the XNAT server, indicating an issue with the server itself. The request will be repeated ${serverIssueRetryCount} more time${serverIssueRetryCount == 1 ? '' : 's'}."
            serverIssueRetryCount.times {
                final Response repeatedResponse = ctx.send(request)
                if (!(repeatedResponse.statusCode in serverIssueCodes)) {
                    repeatedResponse
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

    boolean getStrictSessions() {
        strictSessions
    }

    /**
     * In strict-sessions mode the filter doesn't log in again when XNAT rejects the session: a 401, a redirect to the
     * login page, or the login page served in place of the content each throw {@link XnatSessionRejectedException}.
     * Other redirects are returned untouched. Logging in again after the session has been idle, or after
     * {@link #deleteSessionId()}, and repeating a request after a 502, 503 or 504 work as they do without it.
     */
    void setStrictSessions(boolean strictSessions) {
        this.strictSessions = strictSessions
    }

    void extractSessionId() {
        final Response response = Credentials.build(user).get(CommonStringUtils.formatUrl(xnatUrl, '/data/auth'))
        if (response.statusCode == 200) {
            sessionId = response.getSessionId()
        } else {
            throw new RuntimeException('Provided username and password combination do not appear to be correct')
        }
    }

    protected RequestSpecificationImpl preprocessRequest(RequestSpecification requestSpec) {
        ((allowInsecureSSL) ? requestSpec.relaxedHTTPSValidation() : requestSpec).sessionId(sessionId) as RequestSpecificationImpl
    }

    protected Response issueRequest(RequestSpecification request, FilterContext ctx) {
        ctx.internalRequestUri = URLDecoder.decode(ctx.internalRequestUri, 'UTF-8') // TODO: hack that will need to be revisited when upgrading REST-assured. I'm sorry, Johan
        ctx.send(request)
    }

    protected void failIfSessionRejected(FilterableRequestSpecification requestSpec, Response response) {
        final int status = response.statusCode
        if (status == 401) {
            throw new XnatSessionRejectedException(requestSpec.getMethod(), requestSpec.getURI(), status, XnatSessionRejectedException.Reason.UNAUTHORIZED)
        }
        if (status in REDIRECT_CODES) {
            final String location = response.getHeader('Location')
            if (isLoginLocation(location)) {
                throw new XnatSessionRejectedException(requestSpec.getMethod(), requestSpec.getURI(), status, XnatSessionRejectedException.Reason.REDIRECT_TO_LOGIN, location)
            }
            return // any other redirect, e.g. from a Turbine action, is a normal answer
        }
        if (isLoginPage(response.asString())) {
            throw new XnatSessionRejectedException(requestSpec.getMethod(), requestSpec.getURI(), status, XnatSessionRejectedException.Reason.LOGIN_PAGE_BODY)
        }
    }

    /**
     * Whether a redirect's Location is XNAT's login page: a path segment {@code Login.vm} (as in
     * {@code /app/template/Login.vm}, {@code /app/template/Login.vm/par/...}), or a last path segment {@code login} (Spring
     * Security's {@code /login}). Absolute or relative, with or without a context path; the query, the fragment and any
     * {@code ;jsessionid=...} are ignored, as is case.
     */
    static boolean isLoginLocation(String location) {
        if (location == null) {
            return false
        }
        final String withoutQueryOrFragment = location.trim().split('[?#]', 2)[0]
        final String path = URL_SCHEME_AND_AUTHORITY.matcher(withoutQueryOrFragment).replaceFirst('')
        final List<String> segments = path.split('/').collect { String segment ->
            final int parameters = segment.indexOf(';')
            parameters >= 0 ? segment.substring(0, parameters) : segment
        }.findAll()
        segments.any { it.equalsIgnoreCase('Login.vm') } || (segments && segments.last().equalsIgnoreCase('login'))
    }

    /**
     * Whether a response body is XNAT's login page, recognized by the comment that opens the Login.vm template.
     */
    static boolean isLoginPage(String body) {
        body != null && body.contains('Login.vm') && LOGIN_PAGE_MARKER.matcher(body).find()
    }

}
