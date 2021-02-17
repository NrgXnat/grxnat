package org.nrg.xnat.interfaces

import org.nrg.xnat.pogo.experiments.ImagingSession

class XnatInterface_1_7_7 extends XnatInterface_1_8_0 {

    @Override
    void waitForAutoRun(ImagingSession session, int maxTimeInSeconds = 60) {
        println("A subsequent operation requires that the AutoRun pipeline complete on this session (${session}) before continuing. If this step appears to be hanging, it likely means that the pipeline engine is not configured correctly on the XNAT server. Waiting for AutoRun completion for up to ${maxTimeInSeconds} seconds...")
        waitForPipelineCompletion(session, 'AutoRun', maxTimeInSeconds)
    }

}
