package org.nrg.xnat.pogo.extensions.subject_assessor

import org.hamcrest.Matchers
import org.nrg.xnat.importer.importers.DicomZipRequest
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession

class DicomZipImportExtension extends SubjectAssessorExtension {

    File sessionZip

    DicomZipImportExtension(ImagingSession session, File sessionZip) {
        super(session)
        this.sessionZip = sessionZip
    }

    @Override
    void create(XnatInterface xnatInterface, Project project, Subject subject) {
        final String uri = xnatInterface.callImporter(
                new DicomZipRequest()
                        .file(sessionZip)
                        .destPrearchive()
                        .project(project)
                        .subject(subject)
                        .exptLabel(parentObject as ImagingSession)
        )
        xnatInterface.queryBase().queryParam('action', 'commit').post(xnatInterface.formatXnatUrl(uri))
                .then().assertThat().statusCode(Matchers.oneOf(200, 301))
        xnatInterface.getAccessionNumber(project, parentObject as ImagingSession)
    }

}
