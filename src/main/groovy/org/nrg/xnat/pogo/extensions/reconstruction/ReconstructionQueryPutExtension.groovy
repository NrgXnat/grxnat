package org.nrg.xnat.pogo.extensions.reconstruction

import org.hamcrest.Matchers
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Reconstruction
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.pogo.extensions.session_assessor.SessionAssessorExtension
import org.nrg.xnat.rest.SerializationUtils

class ReconstructionQueryPutExtension extends ReconstructionExtension {

    private XnatInterface xnatInterface

    ReconstructionQueryPutExtension(XnatInterface xnatInterface, Reconstruction reconstruction) {
        super(reconstruction)
        this.xnatInterface = xnatInterface
    }

    @Override
    void create(Project project, Subject subject, ImagingSession session) {
        xnatInterface.queryBase().queryParams(SerializationUtils.serializeToMap(parentObject)).put(xnatInterface.reconstructionUrl(project, subject, session, parentObject as Reconstruction)).then().assertThat().statusCode(Matchers.isOneOf(200, 201))
    }

}
