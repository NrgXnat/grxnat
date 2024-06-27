package org.nrg.xnat.rest

abstract class PermissionsException extends HttpStatusException {

    PermissionsException() {
        super()
    }

    PermissionsException(String message) {
        super(message)
    }

}
