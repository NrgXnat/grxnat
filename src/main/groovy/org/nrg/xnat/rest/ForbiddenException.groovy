package org.nrg.xnat.rest

/**
 * For HTTP status 403
 */
class ForbiddenException extends RuntimeException {

    ForbiddenException() {
        super()
    }

    ForbiddenException(String message) {
        super(message)
    }

}
