package org.nrg.xnat.rest

class UnprocessableContentListener implements CustomExceptionFailureListener {

    @Override
    Class<? extends Exception> getExceptionClass() {
        UnprocessableContentException
    }

}
