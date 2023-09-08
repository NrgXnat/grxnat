package org.nrg.xnat.pogo.dqr

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class DqrImportRequestStudy {

    String studyInstanceUid
    List<String> seriesDescriptions
    List<String> seriesInstanceUids
    Map<String, String> relabelMap

}
