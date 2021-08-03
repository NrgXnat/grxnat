package org.nrg.xnat.importer.params

trait QuarantineRequest<X extends QuarantineRequest<X>> {

    Boolean quarantine

    X quarantine(Boolean setting = true) {
        setQuarantine(setting)
        this as X
    }

}