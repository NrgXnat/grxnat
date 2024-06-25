package org.nrg.xnat.rest

class UnauthorizedListener implements CustomExceptionFailureListener {

    @Override
    Class<? extends Exception> getExceptionClass() {
        UnauthorizedException
    }

}
