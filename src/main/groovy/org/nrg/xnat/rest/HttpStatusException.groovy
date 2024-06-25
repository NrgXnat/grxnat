package org.nrg.xnat.rest

abstract class HttpStatusException extends RuntimeException {

    abstract int getStatusCode()

    HttpStatusException() {
        super()
    }

    HttpStatusException(String message) {
        super(message)
    }

}
