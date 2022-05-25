package org.nrg.xnat.rest

/**
 * For HTTP status 404
 */
class NotFoundException extends RuntimeException {

    NotFoundException() {
        super()
    }

    NotFoundException(String message) {
        super(message)
    }

}
