package org.nrg.xnat.subinterfaces

import com.jayway.restassured.response.Response
import groovyx.gpars.GParsPool
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Share
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.SubjectAssessor
import org.nrg.xnat.pogo.extensions.subject.SubjectQueryPutExtension
import org.nrg.xnat.pogo.resources.SubjectResource

import static org.nrg.testing.CommonStringUtils.formatUrl

class SubjectSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/projects/{PROJECT_ID}/subjects',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}'
        ]
    }

    String projectSubjectsUrl(Project project) {
        formatUrl(xnatInterface.projectUrl(project), 'subjects')
    }

    String subjectUrl(Project project, Subject subject) {
        if (subject.label == null) {
            throw new UnsupportedOperationException('subject.label cannot be null')
        }
        formatUrl(projectSubjectsUrl(project), subject.label)
    }

    String subjectUrl(Subject subject) {
        if (subject.project == null) {
            throw new UnsupportedOperationException('Subject must have project object set.')
        }
        subjectUrl(subject.project, subject)
    }

    String getAccessionNumber(Subject subject) {
        if (subject.project == null) throw new IllegalArgumentException('subject must have project specified')

        subject.accessionNumber ?: subject.accessionNumber(jsonQuery().get(projectSubjectsUrl(subject.project)).jsonPath().getString("ResultSet.Result.find { it.label == '${subject.label}' }.ID")).accessionNumber
    }

    Subject readSubject(String accessionNumber) {
        final Response response = jsonQuery().get(formatRestUrl('subjects', accessionNumber))
        final Subject subject = response.jsonPath().getObject("items.get(0).children.find { it.field == 'demographics' }.items.get(0).data_fields", Subject.class)
        subject.label(response.jsonPath().getString('items.get(0).data_fields.label'))
    }

    List<Subject> readSubjects(Project project) {
        final List<Subject> subjects = subjectQuery(project, true)

        subjects.each { subject ->
            if (xnatInterface.readResources) {
                subject.resources(xnatInterface.readResources(new SubjectResource().project(project).subject(subject)))
            }
            subject.experiments(
                    xnatInterface.readSubjectAssessors(project, subject)
            )
        }
        subjects
        // TODO: shares
    }

    List<Subject> readSecondarySubjects(Project project) {
        subjectQuery(project, false)
        // TODO: fully populate secondary subject objects. Issue: how to handle setting project objects for other projects (i.e. when the project is not the variable "project"). Could make empty project objects, but then attempting to access them later gives incomplete objects.
    }

    private List<Subject> subjectQuery(Project project, boolean primary) {
        jsonQuery().queryParam('columns', 'label,project,gender,handedness,education,race,ethnicity,group,yob,dob,age,height,weight,src').
                get(projectSubjectsUrl(project)).jsonPath().getObject("ResultSet.Result.findAll { it.project ${primary ? '=' : '!'}= '${project.id}' }", Subject[])
    }

    void createSubject(Project project, Subject subject, boolean suppressAssessors = false) {
        if (project == null) {
            throw new UnsupportedOperationException('project cannot be null')
        }
        if (subject == null) {
            throw new UnsupportedOperationException('subject cannot be null')
        }

        if (subject.extension == null) {
            subject.extension(new SubjectQueryPutExtension(subject))
        }

        subject.extension.create(xnatInterface, project)

        subject.resources.each { resource ->
            resource.project(project).subject(subject)
        }
        xnatInterface.uploadResources(subject.resources)

        subject.shares.each { share ->
            shareSubject(project, subject, share)
        }

        xnatInterface.putCustomVariableValues(subjectUrl(project, subject), subject, subject.fields)

        if (suppressAssessors) return

        if (project.isSubjectAssessorParallelization()) {
            GParsPool.withPool {
                subject.experiments.eachParallel { subjectAssessor ->
                    xnatInterface.createSubjectAssessor(project, subject, subjectAssessor as SubjectAssessor)
                }
            }
        } else {
            subject.experiments.each { subjectAssessor ->
                xnatInterface.createSubjectAssessor(project, subject, subjectAssessor)
            }
        }
    }

    void createSubject(Subject subject) {
        if (subject.project == null) {
            throw new UnsupportedOperationException("Subject object must have Project object populated to use this shortcut method")
        }

        createSubject(subject.project, subject)
    }

    void shareSubject(Project sourceProject, Subject subject, Share share) {
        if (share.destinationProject == null) {
            throw new UnsupportedOperationException('Destination project string cannot be null for subject sharing.')
        }

        queryBase().queryParam('label', share.destinationLabel ?: subject.label).put(formatUrl(subjectUrl(sourceProject, subject), "projects/${share.destinationProject}")).
                then().assertThat().statusCode(200)
    }

    void relabelSubject(Project project, Subject subject, String newLabel) {
        queryBase().queryParam('label', newLabel).put(subjectUrl(project, subject)).then().assertThat().statusCode(200)
        subject.setLabel(newLabel)
    }

    void relabelSubject(Subject subject, String newLabel) {
        relabelSubject(subject.project, subject, newLabel)
    }

    void deleteSubject(Project project, Subject subject) {
        queryBase().delete(subjectUrl(project, subject)).then().assertThat().statusCode(200)
    }

    void deleteSubject(Subject subject) {
        deleteSubject(subject.project, subject)
    }

}
