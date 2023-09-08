package org.nrg.xnat.pogo.dqr

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class DqrSettings {

    Boolean allowAllProjectsToUseDqr
    String pacsAvailabilityCheckFrequency
    String assumeSameSessionIfArrivedWithin
    Boolean leavePacsAuditTrail
    Boolean allowAllUsersToUseDqr
    Boolean dicomWebEnabled
    String dqrCallingAe
    Boolean notifyAdminOnImport

}
