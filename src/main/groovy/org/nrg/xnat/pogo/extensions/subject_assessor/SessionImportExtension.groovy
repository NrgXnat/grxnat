package org.nrg.xnat.pogo.extensions.subject_assessor

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession

class SessionImportExtension extends SubjectAssessorExtension {

    XnatInterface xnatInterface
    File sessionZip

    SessionImportExtension(XnatInterface xnatInterface, ImagingSession session, File sessionZip) {
        super(session)
        this.xnatInterface = xnatInterface
        this.sessionZip = sessionZip
    }

    @Override
    void create(Project project, Subject subject) {
        xnatInterface.uploadToSessionZipImporter(sessionZip, project, subject, parentObject as ImagingSession)
    }

}
