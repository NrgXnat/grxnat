package org.nrg.xnat.pogo.resources

import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject

class SubjectResource extends Resource {

    SubjectResource(Project project, Subject subject, String label) {
        setProject(project)
        setSubject(subject)
        setFolder(label)
        if (subject != null) {
            subject.addResource(this)
        }
    }

    SubjectResource() {
        super()
    }

    @Override
    String resourceUrl() {
        "data/projects/${project}/subjects/${subject}"
    }

}
