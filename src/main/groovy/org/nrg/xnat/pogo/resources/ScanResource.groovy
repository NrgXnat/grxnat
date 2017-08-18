package org.nrg.xnat.pogo.resources

import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.Scan

class ScanResource extends Resource {

    ScanResource(Project project, Subject subject, ImagingSession session, Scan scan, String folder) {
        setProject(project)
        setSubject(subject)
        setSubjectAssessor(session)
        setScan(scan)
        if (scan != null) {
            scan.addResource(this)
        }
        setFolder(folder)
    }

    ScanResource(Project project, Subject subject, ImagingSession session, Scan scan) {
        this(project, subject, session, scan, null)
    }

    ScanResource() {}

    @Override
    String resourceUrl() {
        "data/projects/${project}/subjects/${subject}/experiments/${subjectAssessor}/scans/${scan}"
    }

}
