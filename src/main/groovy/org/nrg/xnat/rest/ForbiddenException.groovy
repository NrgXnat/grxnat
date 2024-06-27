package org.nrg.xnat.rest

class ForbiddenException extends PermissionsException {

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
