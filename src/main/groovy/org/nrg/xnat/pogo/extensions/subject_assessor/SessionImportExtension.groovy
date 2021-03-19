package org.nrg.xnat.pogo.extensions.subject_assessor

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession

class SessionImportExtension extends SubjectAssessorExtension {

    File sessionZip

    SessionImportExtension(ImagingSession session, File sessionZip) {
        super(session)
        this.sessionZip = sessionZip
    }

    @Override
    void create(XnatInterface xnatInterface, Project project, Subject subject) {
        xnatInterface.uploadToSessionZipImporter(sessionZip, project, subject, parentObject as ImagingSession)
    }

}
