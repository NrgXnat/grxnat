package org.nrg.xnat.rest

class ForbiddenException extends HttpStatusException {

    ForbiddenException() {
        super()
    }

    ForbiddenException(String message) {
        super(message)
    }

    @Override
    int getStatusCode() {
        403
    }

}
