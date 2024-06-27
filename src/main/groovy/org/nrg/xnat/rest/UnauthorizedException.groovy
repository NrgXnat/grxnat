package org.nrg.xnat.rest

class UnauthorizedException extends PermissionsException {

    UnauthorizedException() {
        super()
    }

    UnauthorizedException(String message) {
        super(message)
    }

    @Override
    int getStatusCode() {
        401
    }

}
