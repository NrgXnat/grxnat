package org.nrg.xnat.subinterfaces

import groovyx.gpars.GParsPool
import org.hamcrest.Matchers
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Share
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.NonimagingAssessor
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.pogo.experiments.SubjectAssessor
import org.nrg.xnat.pogo.extensions.subject_assessor.SessionImportExtension
import org.nrg.xnat.pogo.extensions.subject_assessor.SubjectAssessorQueryPutExtension
import org.nrg.xnat.pogo.resources.SubjectAssessorResource
import org.nrg.xnat.rest.SerializationUtils

import static org.nrg.testing.CommonStringUtils.formatUrl

class SubjectAssessorSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments',
                '/projects/{PROJECT_ID}/experiments'
        ]
    }

    String subjectAssessorsForSubjectUrl(Project project, Subject subject) {
        formatUrl(xnatInterface.subjectUrl(project, subject), 'experiments')
    }

    String subjectAssessorUrl(Project project, Subject subject, SubjectAssessor assessor) {
        if (assessor.label == null) {
            throw new UnsupportedOperationException('assessor.label cannot be null')
        }
        formatUrl(subjectAssessorsForSubjectUrl(project, subject), assessor.label)
    }

    String subjectAssessorUrl(SubjectAssessor assessor) {
        subjectAssessorUrl(assessor.primaryProject ?: assessor.subject.project, assessor.subject, assessor)
    }

    String simplifiedSubjectAssessorUrl(SubjectAssessor subjectAssessor) {
        formatRestUrl("/experiments/${getAccessionNumber(subjectAssessor)}")
    }

    String projectExperimentsUrl(Project project) {
        formatRestUrl("/projects/${project.id}/experiments")
    }

    void createSubjectAssessor(Project project, Subject subject, SubjectAssessor subjectAssessor, boolean suppressAssessors = false) {
        if (project == null) {
            throw new UnsupportedOperationException('project cannot be null')
        }
        if (subject == null) {
            throw new UnsupportedOperationException('subject cannot be null')
        }
        if (subjectAssessor == null) {
            throw new UnsupportedOperationException('subjectAssessor cannot be null')
        }

        if (subjectAssessor.extension == null) {
            subjectAssessor.extension(new SubjectAssessorQueryPutExtension(subjectAssessor))
        }
        subjectAssessor.extension.create(xnatInterface, project, subject)

        subjectAssessor.resources.each { resource ->
            resource.project(project).subject(subject).subjectAssessor(subjectAssessor)
        }
        xnatInterface.uploadResources(subjectAssessor.resources)

        subjectAssessor.shares.each { share ->
            shareSubjectAssessor(subjectAssessor, share)
        }

        xnatInterface.putCustomVariableValues(subjectAssessorUrl(project, subject, subjectAssessor), subjectAssessor, subjectAssessor.fields)

        if (subjectAssessor instanceof ImagingSession) {
            final ImagingSession session = subjectAssessor as ImagingSession
            if (session.scans.size() + session.assessors.size() > 0 && session.extension instanceof SessionImportExtension) {
                xnatInterface.waitForAutoRun(session, 600)
            }

            if (project.isScanParallelization()) {
                GParsPool.withPool {
                    session.scans.eachParallel { scan ->
                        xnatInterface.createScan(project, subject, session, scan as Scan)
                    }
                }
            } else {
                session.scans.each { scan ->
                    xnatInterface.createScan(project, subject, session, scan)
                }
            }

            session.reconstructions.each { reconstruction ->
                xnatInterface.createReconstruction(project, subject, session, reconstruction)
            }

            if (suppressAssessors) return

            if (project.isSessionAssessorParallelization()) {
                GParsPool.withPool {
                    session.assessors.each { assessor ->
                        xnatInterface.createSessionAssessor(project, subject, session, assessor as SessionAssessor)
                    }
                }
            } else {
                session.assessors.each { assessor ->
                    xnatInterface.createSessionAssessor(project, subject, session, assessor)
                }
            }
        }
    }

    void createSubjectAssessor(SubjectAssessor subjectAssessor) {
        createSubjectAssessor(subjectAssessor.primaryProject, subjectAssessor.subject, subjectAssessor)
    }

    String getAccessionNumber(Project project, SubjectAssessor subjectAssessor) {
        subjectAssessor.accessionNumber ?: subjectAssessor.accessionNumber(queryBase().get(projectExperimentsUrl(project)).then().extract().jsonPath().getString("ResultSet.Result.find {it.label == '${subjectAssessor}' }.ID")).accessionNumber
    }

    String getAccessionNumber(SubjectAssessor subjectAssessor) {
        if (subjectAssessor.primaryProject == null) throw new IllegalArgumentException("subjectAssessor object must have project specified.")
        getAccessionNumber(subjectAssessor.primaryProject, subjectAssessor)
    }

    List<SubjectAssessor> readSubjectAssessors(Project project, Subject subject) {
        List imagingMaps, nonimagingMaps

        (imagingMaps, nonimagingMaps) = jsonQuery().queryParam('columns', 'note,date,label,ID').
                get(subjectAssessorsForSubjectUrl(project, subject)).then().assertThat().statusCode(200).
                and().extract().response().jsonPath().getList('ResultSet.Result').split { it.xsiType.matches('xnat:.+SessionData') }

        final List<NonimagingAssessor> nonimagingSubjectAssessors = SerializationUtils.deserializeList(nonimagingMaps, NonimagingAssessor)
        final List<ImagingSession> imagingSessions = SerializationUtils.deserializeList(imagingMaps, ImagingSession)

        //noinspection GroovyAssignabilityCheck
        subject.experiments(nonimagingSubjectAssessors + imagingSessions)

        subject.experiments.each { assessor ->
            if (xnatInterface.readResources) {
                assessor.resources(xnatInterface.readResources(new SubjectAssessorResource().project(project).subject(subject).subjectAssessor(assessor)))
            }
        }

        imagingSessions.each { session ->
            session.scans(xnatInterface.readScans(project, subject, session))
            session.assessors(xnatInterface.readSessionAssessors(project, subject, session))
        }
        subject.experiments
    }

    void shareSubjectAssessor(SubjectAssessor subjectAssessor, Share share) {
        if (share.destinationProject == null) {
            throw new UnsupportedOperationException('Destination project string cannot be null for experiment sharing.')
        }

        queryBase().queryParam('label', share.destinationLabel ?: subjectAssessor.label).
                put(formatUrl(simplifiedSubjectAssessorUrl(subjectAssessor), 'projects', share.destinationProject)).
                then().assertThat().statusCode(Matchers.isOneOf(200, 201))
    }

    void relabelSubjectAssessor(Project project, Subject subject, SubjectAssessor subjectAssessor, String newLabel) {
        queryBase().queryParam('label', newLabel).put(subjectAssessorUrl(project, subject, subjectAssessor)).then().assertThat().statusCode(200)
        subjectAssessor.setLabel(newLabel)
    }

    void relabelSubjectAssessor(SubjectAssessor subjectAssessor, String newLabel) {
        relabelSubjectAssessor(subjectAssessor.primaryProject, subjectAssessor.subject, subjectAssessor, newLabel)
    }

    void moveSubjectAssessorToOtherSubject(SubjectAssessor subjectAssessor, Subject destinationSubject) {
        queryBase().queryParam('subject_ID', destinationSubject.label).put(subjectAssessorUrl(subjectAssessor)).then().assertThat().statusCode(200)
        subjectAssessor.subject.removeExperiment(subjectAssessor)
        subjectAssessor.setSubject(destinationSubject)
    }

    void deleteSubjectAssessor(Project project, Subject subject, SubjectAssessor subjectAssessor) {
        queryBase().queryParam('removeFiles', true).delete(subjectAssessorUrl(project, subject, subjectAssessor)).then().assertThat().statusCode(200)
    }

    void deleteSubjectAssessor(SubjectAssessor subjectAssessor) {
        deleteSubjectAssessor(subjectAssessor.primaryProject, subjectAssessor.subject, subjectAssessor)
    }

}
