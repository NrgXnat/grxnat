package org.nrg.xnat.rest

/**
 * Thrown by {@link XnatSessionFilter} in strict-sessions mode when XNAT rejects the session a request carried. Outside
 * strict-sessions mode the filter logs in again and repeats the request instead, which hides a lost session.
 */
class XnatSessionRejectedException extends RuntimeException {

    enum Reason {
        UNAUTHORIZED('XNAT answered 401 Unauthorized'),
        REDIRECT_TO_LOGIN('XNAT redirected to the login page'),
        LOGIN_PAGE_BODY('XNAT served the login page in place of the content')

        final String description

        Reason(String description) {
            this.description = description
        }
    }

    final String method
    final String uri
    final int statusCode
    final Reason reason
    final String location

    XnatSessionRejectedException(String method, String uri, int statusCode, Reason reason, String location = null) {
        super(composeMessage(method, uri, statusCode, reason, location))
        this.method = method
        this.uri = uri
        this.statusCode = statusCode
        this.reason = reason
        this.location = location
    }

    private static String composeMessage(String method, String uri, int statusCode, Reason reason, String location) {
        final String detail = location == null ? reason.description : "${reason.description} (Location: ${location})"
        "XNAT rejected the session on ${method} ${uri}: HTTP ${statusCode}, ${detail}. Strict sessions is on, so the request fails instead of logging in again."
    }

}
