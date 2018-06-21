package org.nrg.xnat.pogo.resources

import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Reconstruction
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.SessionAssessor

class ReconstructionResource extends Resource {

    ReconstructionResource(Project project, Subject subject, ImagingSession session, Reconstruction reconstruction, String folder) {
        setProject(project)
        setSubject(subject)
        setSubjectAssessor(session)
        if (reconstruction != null) {
            setReconstruction(reconstruction)
            reconstruction.addResource(this)
        }
        setFolder(folder)
    }

    ReconstructionResource(Project project, Subject subject, ImagingSession session, Reconstruction reconstruction) {
        this(project, subject, session, reconstruction, null)
    }

    ReconstructionResource() {}

    @Override
    String resourceUrl() {
        "data/projects/${project}/subjects/${subject}/experiments/${subjectAssessor}/reconstructions/${reconstruction}"
    }

}
