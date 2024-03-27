package org.nrg.xnat.pogo.dqr

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class DqrSettings1x extends DqrSettings {
    @JsonProperty("dqrMaxPacsCMOVEAttempts") String dqrMaxPacsRequestAttempts
    @JsonProperty("dqrWaitToRetryCMOVETimeInSeconds") String dqrWaitToRetryRequestInSeconds
}
