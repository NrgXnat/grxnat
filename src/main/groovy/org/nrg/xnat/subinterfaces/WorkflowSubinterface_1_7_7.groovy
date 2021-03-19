package org.nrg.xnat.subinterfaces

import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.versions.XnatVersion
import org.nrg.xnat.versions.XnatVersionList

class WorkflowSubinterface_1_7_7 extends WorkflowSubinterface {

    @Override
    List<Class<? extends XnatVersion>> supportedVersions() {
        XnatVersionList.VERSIONS_BEFORE_1_8
    }

    @Override
    void waitForAutoRun(ImagingSession session, int maxTimeInSeconds = 60) {
        println("A subsequent operation requires that the AutoRun pipeline complete on this session (${session}) before continuing. If this step appears to be hanging, it likely means that the pipeline engine is not configured correctly on the XNAT server. Waiting for AutoRun completion for up to ${maxTimeInSeconds} seconds...")
        waitForPipelineCompletion(session, 'AutoRun', maxTimeInSeconds)
    }

}
