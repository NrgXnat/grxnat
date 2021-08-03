package org.nrg.xnat.importer.params

import com.fasterxml.jackson.annotation.JsonProperty
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession

trait ManualRoutingRequest<X extends ManualRoutingRequest<X>> {

    @JsonProperty('PROJECT_ID') String projectId
    @JsonProperty('SUBJECT_ID') String subjectId
    @JsonProperty('EXPT_LABEL') String exptLabel
    String project


    X project(Project project) {
        setProject(project.id)
        setProjectId(project.id)
        this as X
    }

    X subject(Subject subjectObj) {
        subject(subjectObj.label)
    }

    X subject(String label) {
        setSubjectId(label)
        this as X
    }

    X exptLabel(ImagingSession session) {
        exptLabel(session.label)
    }

    X exptLabel(String sessionLabel) {
        setExptLabel(sessionLabel)
        this as X
    }

}