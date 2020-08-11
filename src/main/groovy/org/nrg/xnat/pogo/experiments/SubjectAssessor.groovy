package org.nrg.xnat.pogo.experiments

import org.nrg.xnat.pogo.HasUri
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.extensions.subject_assessor.SubjectAssessorExtension
import org.nrg.xnat.util.RandomUtils
import org.nrg.xnat.util.XnatUriUtils

abstract class SubjectAssessor extends Experiment implements HasUri {

    SubjectAssessor(Project project, Subject subject, String label) {
        super()
        setPrimaryProject(project)
        setSubject(subject)
        this.label = label ?: RandomUtils.randomID()
    }

    SubjectAssessor() {
        super()
    }

    @Override
    String getUri() {
        XnatUriUtils.subjectAssessorUri(
                (primaryProject ?: subject.primaryProject).id,
                subject.label,
                label,
                true
        )
    }

    @Override
    void setSubject(Subject subject) {
        super.setSubject(subject)
        if (subject != null) subject.addExperiment(this)
    }

    SubjectAssessorExtension getExtension() {
        return (SubjectAssessorExtension)super.getExtension()
    }

    void setExtension(SubjectAssessorExtension extension) {
        super.setExtension(extension)
    }

    def <T extends Experiment> T extension(SubjectAssessorExtension extension) {
        return super.extension(extension)
    }

}
