package org.nrg.xnat.pogo.dqr

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
@JsonInclude(JsonInclude.Include.NON_NULL)
class DqrSettings {

    String dqrCallingAe
    String pacsAvailabilityCheckFrequency
    String dqrMaxPacsRequestAttempts
    String dqrWaitToRetryRequestInSeconds
    Boolean notifyAdminOnImport
    String assumeSameSessionIfArrivedWithin
    Boolean allowAllUsersToUseDqr
    Boolean allowAllProjectsToUseDqr
    String ormStrategy
    String patientNameStrategy
    String resultSetLimitStrategy
    Boolean leavePacsAuditTrail

    DqrSettings dqrMaxPacsRequestAttempts(int dqrMaxPacsRequestAttempts) {
        this.dqrMaxPacsRequestAttempts = String.valueOf(dqrMaxPacsRequestAttempts)
        this
    }

    DqrSettings dqrWaitToRetryRequestInSeconds(int dqrWaitToRetryRequestInSeconds) {
        this.dqrWaitToRetryRequestInSeconds = String.valueOf(dqrWaitToRetryRequestInSeconds)
        this
    }
}
