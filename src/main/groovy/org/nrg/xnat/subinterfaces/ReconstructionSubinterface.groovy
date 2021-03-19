package org.nrg.xnat.subinterfaces

import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Reconstruction
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.extensions.reconstruction.ReconstructionQueryPutExtension

import static org.nrg.testing.CommonStringUtils.formatUrl

class ReconstructionSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{ASSESSED_ID}/reconstructions/{RECON_ID}'
        ]
    }

    String reconstructionUrl(Project project, Subject subject, ImagingSession session, Reconstruction reconstruction) {
        if (reconstruction.label == null) {
            throw new UnsupportedOperationException('reconstruction.label cannot be null')
        }
        formatUrl(xnatInterface.subjectAssessorUrl(project, subject, session), 'reconstructions', reconstruction.label)
    }

    String reconstructionUrl(Reconstruction reconstruction) {
        formatUrl(xnatInterface.subjectAssessorUrl(reconstruction.parentSession), 'reconstructions', reconstruction.label)
    }

    void createReconstruction(Project project, Subject subject, ImagingSession session, Reconstruction reconstruction) {
        if (project == null) {
            throw new UnsupportedOperationException('project cannot be null')
        }
        if (subject == null) {
            throw new UnsupportedOperationException('subject cannot be null')
        }
        if (session == null) {
            throw new UnsupportedOperationException('session cannot be null')
        }
        if (reconstruction == null) {
            throw new UnsupportedOperationException('reconstruction cannot be null')
        }

        if (reconstruction.extension == null) {
            reconstruction.extension(new ReconstructionQueryPutExtension(reconstruction))
        }

        reconstruction.extension.create(xnatInterface, project, subject, session)

        reconstruction.resources.each { resource ->
            resource.project(project).subject(subject).subjectAssessor(session).reconstruction(reconstruction)
        }
        xnatInterface.uploadResources(reconstruction.resources)
    }

    void createReconstruction(Reconstruction reconstruction) {
        final ImagingSession session = reconstruction.parentSession
        createReconstruction(session.primaryProject, session.subject, session, reconstruction)
    }

}
