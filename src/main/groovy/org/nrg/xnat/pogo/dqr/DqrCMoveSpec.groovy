package org.nrg.xnat.pogo.dqr

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class DqrCMoveSpec {

    String pacsId
    String aeTitle
    String port
    String projectId
    boolean forceImport
    List<DqrImportRequestStudy> studies = []

    static DqrCMoveSpec toPacs(PacsConnection pacsConnection) {
        new DqrCMoveSpec(pacsId: String.valueOf(pacsConnection.id))
    }

    DqrCMoveSpec addStudy(DqrImportRequestStudy study) {
        studies << study
        this
    }

}