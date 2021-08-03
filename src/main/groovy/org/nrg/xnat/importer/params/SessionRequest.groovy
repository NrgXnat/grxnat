package org.nrg.xnat.importer.params

import org.nrg.xnat.pogo.experiments.ImagingSession

trait SessionRequest<X extends SessionRequest<X>> {

    String session

    X session(String label) {
        setSession(label)
        this as X
    }

    X session(ImagingSession session) {
        session(session.label)
    }

}