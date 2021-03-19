package org.nrg.xnat.subinterfaces

import org.hamcrest.Matchers
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Share
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.pogo.extensions.session_assessor.SessionAssessorQueryPutExtension
import org.nrg.xnat.pogo.resources.SessionAssessorResource

import static org.nrg.testing.CommonStringUtils.formatUrl

class SessionAssessorSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/experiments/{ASSESSED_ID}/assessors/{EXPT_ID}',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{ASSESSED_ID}/assessors',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{ASSESSED_ID}/assessors/{EXPT_ID}',
        ]
    }

    String assessorsUrl(Project project, Subject subject, ImagingSession session) {
        formatUrl(xnatInterface.subjectAssessorUrl(project, subject, session), 'assessors')
    }

    String sessionAssessorUrl(Project project, Subject subject, ImagingSession session, SessionAssessor sessionAssessor) {
        if (sessionAssessor.label == null) {
            throw new UnsupportedOperationException('sessionAssessor.label cannot be null')
        }
        formatUrl(assessorsUrl(project, subject, session), sessionAssessor.label)
    }

    String sessionAssessorUrl(SessionAssessor assessor) {
        sessionAssessorUrl(assessor.primaryProject, assessor.subject, assessor.parentSession, assessor)
    }

    String assessorsUrlByAccessionNumber(ImagingSession session) {
        if (session.accessionNumber == null) {
            throw new UnsupportedOperationException('Method requires session object to have accessionNumber populated')
        }

        formatUrl(xnatInterface.simplifiedSubjectAssessorUrl(session), '/assessors')
    }

    String assessorUrlByAccessionNumber(ImagingSession session, SessionAssessor assessor) {
        if (assessor.getAccessionNumber() == null) {
            throw new UnsupportedOperationException("Method requires assessor object to have accessionNumber populated.")
        }
        formatUrl(assessorsUrlByAccessionNumber(session), assessor.accessionNumber)
    }

    List<SessionAssessor> readSessionAssessors(Project project, Subject subject, ImagingSession session) {
        jsonQuery().get(assessorsUrl(project, subject, session)).jsonPath().getObject('ResultSet.Result', SessionAssessor[]).collect { assessor ->
            if (xnatInterface.readResources) {
                assessor.resources(xnatInterface.readResources(new SessionAssessorResource().project(project).subject(subject).subjectAssessor(session).sessionAssessor(assessor)))
            }
            assessor as SessionAssessor
        }
    }

    void createSessionAssessor(Project project, Subject subject, ImagingSession session, SessionAssessor assessor) {
        if (project == null) {
            throw new UnsupportedOperationException("project cannot be null")
        }
        if (subject == null) {
            throw new UnsupportedOperationException("subject cannot be null")
        }
        if (session == null) {
            throw new UnsupportedOperationException("session cannot be null")
        }
        if (assessor == null) {
            throw new UnsupportedOperationException("assessor cannot be null")
        }

        if (assessor.extension == null) {
            assessor.extension(new SessionAssessorQueryPutExtension(assessor))
        }

        assessor.extension.create(xnatInterface, project, subject, session)

        assessor.shares.each { share ->
            shareSessionAssessor(session, assessor, share)
        }

        assessor.resources.each { resource ->
            resource.project(project).subject(subject).subjectAssessor(session).sessionAssessor(assessor)
        }
        xnatInterface.uploadResources(assessor.resources)

        xnatInterface.putCustomVariableValues(sessionAssessorUrl(project, subject, session, assessor), assessor, assessor.fields)
    }

    void createSessionAssessor(SessionAssessor assessor) {
        createSessionAssessor(assessor.primaryProject, assessor.subject, assessor.parentSession, assessor)
    }

    void shareSessionAssessor(ImagingSession session, SessionAssessor sessionAssessor, Share share) {
        if (share.destinationProject == null) {
            throw new UnsupportedOperationException('Destination project string cannot be null for experiment sharing.')
        }

        queryBase().queryParam('label', share.destinationLabel ?: sessionAssessor.label).put(formatUrl(assessorUrlByAccessionNumber(session, sessionAssessor), 'projects', share.destinationProject)).
                then().assertThat().statusCode(Matchers.isOneOf(200, 201))
    }

    void deleteSessionAssessor(Project project, Subject subject, ImagingSession session, SessionAssessor sessionAssessor) {
        queryBase().delete(sessionAssessorUrl(project, subject, session, sessionAssessor)).then().assertThat().statusCode(200)
    }

    void deleteSessionAssessor(SessionAssessor sessionAssessor) {
        deleteSessionAssessor(sessionAssessor.getPrimaryProject(), sessionAssessor.getSubject(), sessionAssessor.getParentSession(), sessionAssessor)
    }

}
