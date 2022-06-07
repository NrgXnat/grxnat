package org.nrg.xnat.rest

class ForbiddenListener implements CustomExceptionFailureListener {

    @Override
    Class<? extends Exception> getExceptionClass() {
        ForbiddenException
    }

    @Override
    int getMappedStatusCode() {
        403
    }

}
