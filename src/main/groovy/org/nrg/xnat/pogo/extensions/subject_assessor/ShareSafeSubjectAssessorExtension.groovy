package org.nrg.xnat.pogo.extensions.subject_assessor

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.SubjectAssessor

class ShareSafeSubjectAssessorExtension extends SubjectAssessorQueryPutExtension {

    ShareSafeSubjectAssessorExtension(XnatInterface xnatInterface, SubjectAssessor subjectAssessor) {
        super(xnatInterface, subjectAssessor)
    }

    @Override
    String url(Project project, Subject subject, SubjectAssessor subjectAssessor) {
        xnatInterface.formatRestUrl("/projects/${subjectAssessor.getPrimaryProject() ?: project}/subjects/${xnatInterface.getAccessionNumber(subject)}/experiments/${subjectAssessor.label}")
    }

}
