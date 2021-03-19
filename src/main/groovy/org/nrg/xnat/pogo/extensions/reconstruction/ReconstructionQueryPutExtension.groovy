package org.nrg.xnat.pogo.extensions.reconstruction

import org.hamcrest.Matchers
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Reconstruction
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.rest.SerializationUtils

class ReconstructionQueryPutExtension extends ReconstructionExtension {

    ReconstructionQueryPutExtension(Reconstruction reconstruction) {
        super(reconstruction)
    }

    @Override
    void create(XnatInterface xnatInterface, Project project, Subject subject, ImagingSession session) {
        xnatInterface.queryBase().queryParams(SerializationUtils.serializeToMap(parentObject)).
                put(xnatInterface.reconstructionUrl(project, subject, session, parentObject as Reconstruction)).
                then().assertThat().statusCode(Matchers.isOneOf(200, 201))
    }

}
