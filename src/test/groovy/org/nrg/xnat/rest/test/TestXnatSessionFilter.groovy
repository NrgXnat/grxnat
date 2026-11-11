package org.nrg.xnat.rest.test

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import io.restassured.RestAssured
import io.restassured.response.Response
import org.nrg.xnat.XnatConnectionConfig
import org.nrg.xnat.pogo.users.User
import org.nrg.xnat.rest.XnatSessionFilter
import org.nrg.xnat.rest.XnatSessionRejectedException
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import java.nio.charset.StandardCharsets

import static org.testng.AssertJUnit.assertEquals
import static org.testng.AssertJUnit.assertFalse
import static org.testng.AssertJUnit.assertTrue
import static org.testng.AssertJUnit.fail

/**
 * Runs {@link XnatSessionFilter} through REST-assured against a stand-in XNAT. {@code /data/auth} hands out
 * JSESSIONID=session-1, session-2, ... and every other path answers from {@link #answers}: a path's answers are used in
 * order, and its last answer repeats.
 */
class TestXnatSessionFilter {

    private static final String LOGIN_PAGE = '''\
        <!DOCTYPE html>
        <html><body>
        <!-- BEGIN xnat-templates/screens/Login.vm -->
        <form name="form1" method="post" action="/login" id="login_form"></form>
        <!-- END xnat-templates/screens/Login.vm -->
        </body></html>
        '''.stripIndent()

    private HttpServer server
    private String baseUrl
    private int logins
    private final Map<String, List<Answer>> answers = [:]
    private final List<String> requests = []
    private final List<String> sessionCookies = []

    @BeforeClass
    void startServer() {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0)
        server.createContext('/') { HttpExchange exchange ->
            try {
                handle(exchange)
            } finally {
                exchange.close()
            }
        }
        server.start()
        baseUrl = "http://${server.address.address.hostAddress}:${server.address.port}"
    }

    @AfterClass(alwaysRun = true)
    void stopServer() {
        server?.stop(0)
    }

    @BeforeMethod
    void reset() {
        synchronized (this) {
            logins = 0
            answers.clear()
            requests.clear()
            sessionCookies.clear()
        }
    }

    @Test
    void testStrictSessionsIsOffByDefault() {
        assertFalse(new XnatConnectionConfig().strictSessions)
        assertFalse(newFilter(false).strictSessions)
    }

    @Test
    void testStrictFailsOn401() {
        answer('/data/projects', new Answer(401))
        final XnatSessionFilter filter = newFilter(true)

        final XnatSessionRejectedException exception = expectRejection { get(filter, '/data/projects?format=json') }

        assertEquals(XnatSessionRejectedException.Reason.UNAUTHORIZED, exception.reason)
        assertEquals('GET', exception.method)
        assertEquals("${baseUrl}/data/projects?format=json".toString(), exception.uri)
        assertEquals(401, exception.statusCode)
        assertTrue(exception.message, exception.message.contains("GET ${baseUrl}/data/projects?format=json"))
        assertTrue(exception.message, exception.message.contains('HTTP 401'))
        assertTrue(exception.message, exception.message.contains('401 Unauthorized'))
        assertTrue(exception.message, exception.message.contains('Strict sessions is on'))
        assertEquals(1, logins) // only the filter's initial login
        assertEquals(['GET /data/projects'], requests)
    }

    @Test
    void testStrictFailsOnRedirectToLogin() {
        final String location = "${baseUrl}/app/template/Login.vm;jsessionid=ABC123"
        answer('/app/action/ModifyProject', new Answer(302, location))
        final XnatSessionFilter filter = newFilter(true)

        // REST-assured follows a 302 only for GET and HEAD, so the filter sees this one
        final XnatSessionRejectedException exception = expectRejection { post(filter, '/app/action/ModifyProject') }

        assertEquals(XnatSessionRejectedException.Reason.REDIRECT_TO_LOGIN, exception.reason)
        assertEquals('POST', exception.method)
        assertEquals(302, exception.statusCode)
        assertEquals(location, exception.location)
        assertTrue(exception.message, exception.message.contains("POST ${baseUrl}/app/action/ModifyProject"))
        assertTrue(exception.message, exception.message.contains('HTTP 302'))
        assertTrue(exception.message, exception.message.contains('login page'))
        assertTrue(exception.message, exception.message.contains(location))
        assertEquals(1, logins)
        assertEquals(['POST /app/action/ModifyProject'], requests)
    }

    @Test
    void testStrictFailsOnLoginPageBody() {
        answer('/data/experiments', new Answer(200, null, LOGIN_PAGE))
        final XnatSessionFilter filter = newFilter(true)

        final XnatSessionRejectedException exception = expectRejection { get(filter, '/data/experiments') }

        assertEquals(XnatSessionRejectedException.Reason.LOGIN_PAGE_BODY, exception.reason)
        assertEquals(200, exception.statusCode)
        assertTrue(exception.message, exception.message.contains("GET ${baseUrl}/data/experiments"))
        assertTrue(exception.message, exception.message.contains('login page'))
        assertEquals(1, logins)
    }

    @Test
    void testStrictFailsOnFollowedRedirectToLoginPage() {
        answer('/app/template/XDATScreen_admin.vm', new Answer(302, '/app/template/Login.vm'))
        answer('/app/template/Login.vm', new Answer(200, null, LOGIN_PAGE))
        final XnatSessionFilter filter = newFilter(true)

        // a GET's redirect is followed, so what the filter sees is the login page itself
        final XnatSessionRejectedException exception = expectRejection { get(filter, '/app/template/XDATScreen_admin.vm') }

        assertEquals(XnatSessionRejectedException.Reason.LOGIN_PAGE_BODY, exception.reason)
        assertEquals(1, logins)
        assertEquals(['GET /app/template/XDATScreen_admin.vm', 'GET /app/template/Login.vm'], requests)
    }

    @Test
    void testStrictPassesOtherRedirectsThrough() {
        final String location = '/app/action/DisplayItemAction/search_element/xnat:projectData/search_field/xnat:projectData.ID/search_value/P1'
        answer('/app/action/AddProject', new Answer(302, location))
        final XnatSessionFilter filter = newFilter(true)

        final Response response = post(filter, '/app/action/AddProject')

        assertEquals(302, response.statusCode)
        assertEquals(location, response.getHeader('Location'))
        assertEquals(1, logins)
        assertEquals(['POST /app/action/AddProject'], requests)
    }

    @Test
    void testStrictPassesOrdinaryResponsesThrough() {
        // pages that only mention the login page, e.g. the JavaScript some pages use to go there after a 401, aren't it
        final String pageMentioningLogin = '<script>if (o.status == 401) { window.location = serverRoot + "/app/template/Login.vm"; }</script>'
        answer('/app/template/Page.vm', new Answer(200, null, pageMentioningLogin))
        answer('/data/projects/MISSING', new Answer(404, null, 'not found'))
        answer('/data/projects/SECRET', new Answer(403, null, 'forbidden'))
        final XnatSessionFilter filter = newFilter(true)

        assertEquals(pageMentioningLogin, get(filter, '/app/template/Page.vm').asString())
        assertEquals(404, get(filter, '/data/projects/MISSING').statusCode)
        assertEquals(403, get(filter, '/data/projects/SECRET').statusCode)
        assertEquals(1, logins)
    }

    @Test
    void testStrictStillLogsInWhenThereIsNoSession() {
        answer('/data/projects', new Answer(200, null, 'ok'))
        final XnatSessionFilter filter = newFilter(true)
        filter.deleteSessionId()

        assertEquals('ok', get(filter, '/data/projects').asString())
        assertEquals(2, logins)
        assertEquals(['JSESSIONID=session-2'], sessionCookies)
    }

    @Test
    void testStrictStillRepeatsServerErrors() {
        answer('/data/projects', new Answer(503), new Answer(200, null, 'ok'))
        final XnatSessionFilter filter = newFilter(true)

        get(filter, '/data/projects')

        assertEquals(['GET /data/projects', 'GET /data/projects'], requests)
        assertEquals(1, logins)
    }

    @Test
    void testDefaultLogsInAgainOn401() {
        answer('/data/projects', new Answer(401), new Answer(200, null, 'ok'))
        final XnatSessionFilter filter = newFilter(false)

        assertEquals('ok', get(filter, '/data/projects').asString())
        assertEquals(2, logins)
        assertEquals(['JSESSIONID=session-1', 'JSESSIONID=session-2'], sessionCookies)
    }

    @Test
    void testDefaultLogsInAgainOnAny302() {
        answer('/app/action/AddProject', new Answer(302, '/app/template/XDATScreen_report_xnat_projectData.vm'), new Answer(200, null, 'ok'))
        final XnatSessionFilter filter = newFilter(false)

        assertEquals('ok', post(filter, '/app/action/AddProject').asString())
        assertEquals(2, logins)
        assertEquals(['JSESSIONID=session-1', 'JSESSIONID=session-2'], sessionCookies)
    }

    @Test
    void testDefaultLogsInAgainOnLoginPageBody() {
        answer('/data/experiments', new Answer(200, null, LOGIN_PAGE), new Answer(200, null, 'ok'))
        final XnatSessionFilter filter = newFilter(false)

        assertEquals('ok', get(filter, '/data/experiments').asString())
        assertEquals(2, logins)
    }

    @DataProvider(name = 'locations')
    Object[][] locations() {
        [
                ['/app/template/Login.vm', true],
                ['http://xnat.example.org/app/template/Login.vm', true],
                ['https://xnat.example.org:8443/xnat/app/template/Login.vm?failed=true', true],
                ['/app/template/Login.vm;jsessionid=ABC123', true],
                ['/app/template/Login.vm/par/abc123', true],
                ['/app/template/Login.vm#!', true],
                ['//xnat.example.org/app/template/login.vm', true],
                ['/login', true],
                ['https://xnat.example.org/xnat/login?error', true],
                ['/login;jsessionid=ABC123', true],
                ['/login/', true],
                ['/app/template/ForgotLogin.vm', false],
                ['/app/template/RegisterExternalLogin.vm', false],
                ['/app/action/XDATLoginUser', false],
                ['/app/template/XDATScreen_report_xnat_projectData.vm/search_element/xnat:projectData', false],
                ['/app/template/Index.vm', false],
                ['/data/projects/login/subjects', false],
                ['https://login.example.org/app/template/Index.vm', false],
                ['/app/template/Index.vm?next=/app/template/Login.vm', false],
                ['/', false],
                ['', false],
                [null, false]
        ] as Object[][]
    }

    @Test(dataProvider = 'locations')
    void testLoginLocation(String location, boolean isLogin) {
        assertEquals(location, isLogin, XnatSessionFilter.isLoginLocation(location))
    }

    @DataProvider(name = 'bodies')
    Object[][] bodies() {
        [
                [LOGIN_PAGE, true],
                ['<!-- BEGIN xnat-templates/screens/Login.vm -->', true],
                ['<!--BEGIN /xnat-templates/screens/Login.vm-->', true],
                ['<!-- BEGIN xnat-templates/screens/ForgotLogin.vm -->', false],
                ['<!-- BEGIN xnat-templates/screens/Login.vm@displayForgotLogin -->', false],
                ['<a href="/app/template/Login.vm">Login</a>', false],
                ['window.location = serverRoot + "/app/template/Login.vm";', false],
                ['{"ResultSet":{"Result":[]}}', false],
                ['', false],
                [null, false]
        ] as Object[][]
    }

    @Test(dataProvider = 'bodies')
    void testLoginPage(String body, boolean isLogin) {
        assertEquals(String.valueOf(body), isLogin, XnatSessionFilter.isLoginPage(body))
    }

    private XnatSessionFilter newFilter(boolean strict) {
        final XnatSessionFilter filter = new XnatSessionFilter(new User('user').password('pass'), baseUrl, false)
        filter.strictSessions = strict
        filter
    }

    private Response get(XnatSessionFilter filter, String path) {
        RestAssured.given().filter(filter).get("${baseUrl}${path}")
    }

    private Response post(XnatSessionFilter filter, String path) {
        RestAssured.given().filter(filter).post("${baseUrl}${path}")
    }

    private static XnatSessionRejectedException expectRejection(Closure request) {
        try {
            request()
        } catch (XnatSessionRejectedException e) {
            return e
        }
        fail('Expected the request to fail with XnatSessionRejectedException')
        null
    }

    private void answer(String path, Answer... pathAnswers) {
        synchronized (this) {
            answers[path] = new ArrayList<Answer>(Arrays.asList(pathAnswers))
        }
    }

    private void handle(HttpExchange exchange) {
        exchange.requestBody.bytes // drain it
        final String path = exchange.requestURI.path
        Answer answer
        synchronized (this) {
            if (path == '/data/auth') {
                logins++
                exchange.responseHeaders.add('Set-Cookie', "JSESSIONID=session-${logins}; Path=/")
                send(exchange, new Answer(200, null, "session-${logins}"))
                return
            }
            requests << "${exchange.requestMethod} ${path}".toString()
            final String cookie = exchange.requestHeaders.getFirst('Cookie')
            if (cookie != null) {
                sessionCookies << cookie
            }
            final List<Answer> pathAnswers = answers[path]
            answer = pathAnswers == null ? new Answer(404) : (pathAnswers.size() > 1 ? pathAnswers.remove(0) : pathAnswers[0])
        }
        send(exchange, answer)
    }

    private static void send(HttpExchange exchange, Answer answer) {
        if (answer.location != null) {
            exchange.responseHeaders.add('Location', answer.location)
        }
        final byte[] body = (answer.body ?: '').getBytes(StandardCharsets.UTF_8)
        if (body.length > 0) {
            exchange.responseHeaders.add('Content-Type', 'text/html; charset=UTF-8')
        }
        exchange.sendResponseHeaders(answer.status, body.length > 0 ? body.length : -1)
        if (body.length > 0) {
            exchange.responseBody.write(body)
        }
    }

    private static class Answer {
        final int status
        final String location
        final String body

        Answer(int status, String location = null, String body = null) {
            this.status = status
            this.location = location
            this.body = body
        }
    }

}
