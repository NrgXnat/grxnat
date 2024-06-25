package org.nrg.xnat.rest

class NotFoundListener implements CustomExceptionFailureListener {

    @Override
    Class<? extends Exception> getExceptionClass() {
        NotFoundException
    }

}
