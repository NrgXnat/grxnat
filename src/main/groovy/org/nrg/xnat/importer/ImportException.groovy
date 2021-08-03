package org.nrg.xnat.importer

class ImportException extends RuntimeException {

    ImportException(int statusCode, String message) {
        super(message)
        setStatusCode(statusCode)
    }

    int statusCode

}
