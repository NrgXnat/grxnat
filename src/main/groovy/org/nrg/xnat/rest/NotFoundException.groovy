package org.nrg.xnat.rest

class NotFoundException extends HttpStatusException {

    NotFoundException() {
        super()
    }

    NotFoundException(String message) {
        super(message)
    }

    @Override
    int getStatusCode() {
        404
    }

}
