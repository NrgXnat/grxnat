package org.nrg.xnat.pogo.resources

import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.SubjectAssessor

class SubjectAssessorResource extends Resource {

    SubjectAssessorResource(Project project, Subject subject, SubjectAssessor subjectAssessor, String label) {
        setProject(project)
        setSubject(subject)
        setSubjectAssessor(subjectAssessor)
        setFolder(label)
        if (subjectAssessor != null) {
            subjectAssessor.addResource(this)
        }
    }

    SubjectAssessorResource() {
        super()
    }


    @Override
    String resourceUrl() {
        "data/projects/${project}/subjects/${subject}/experiments/${subjectAssessor}"
    }

}
