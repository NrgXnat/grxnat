package org.nrg.xnat.rest

class UnprocessableContentException extends HttpStatusException {

    UnprocessableContentException() {
        super()
    }

    UnprocessableContentException(String message) {
        super(message)
    }

    @Override
    int getStatusCode() {
        422
    }

}
