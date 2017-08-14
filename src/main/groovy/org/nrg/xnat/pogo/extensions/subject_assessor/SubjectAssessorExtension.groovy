package org.nrg.xnat.pogo.extensions.subject_assessor

import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.Experiment
import org.nrg.xnat.pogo.experiments.SubjectAssessor

abstract class SubjectAssessorExtension extends Extension<Experiment> {

    SubjectAssessorExtension(SubjectAssessor subjectAssessor) {
        super(subjectAssessor)
    }

    abstract void create(Project project, Subject subject)

}
