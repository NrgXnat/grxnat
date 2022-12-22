package org.nrg.xnat.subinterfaces

import groovyx.gpars.GParsPool
import io.restassured.path.json.JsonPath
import org.nrg.xnat.enums.XnatRecursionLevel
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Share
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.SubjectAssessor
import org.nrg.xnat.pogo.extensions.subject.SubjectQueryPutExtension
import org.nrg.xnat.pogo.resources.SubjectResource

import static org.nrg.testing.CommonStringUtils.formatUrl

class SubjectSubinterface extends XnatFunctionalitySubinterface {

    private static final String FIELD_JSONPATH = subfieldJsonpath('fields/field')
    private static final String SHARING_JSONPATH = subfieldJsonpath('sharing/share')

    @Override
    List<String> getHandledEndpoints() {
        [
                '/projects/{PROJECT_ID}/subjects',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}',
                '/subjects/{SUBJECT_ID}'
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
        final JsonPath subjectJsonPath = readSubjectJsonPath(accessionNumber)
        final Subject subject = subjectJsonPath.getObject("children.find { it.field == 'demographics' }.items[0].data_fields", Subject)
        extractSubjectData(subject, subjectJsonPath)
        subject
    }

    List<Subject> readSubjects(Project project, XnatRecursionLevel recursion = XnatRecursionLevel.FULL_RECURSION) {
        subjectQuery(project, true, recursion)
    }

    List<Subject> readSecondarySubjects(Project project, XnatRecursionLevel recursion = XnatRecursionLevel.FULL_RECURSION) {
        subjectQuery(project, false, recursion)
    }

    List<Subject> readPrimaryAndSecondarySubjects(Project project, XnatRecursionLevel recursion = XnatRecursionLevel.FULL_RECURSION) {
        List<Subject> subjects = new ArrayList<>()
        subjects.addAll(readSubjects(project, recursion))
        subjects.addAll(readSecondarySubjects(project, recursion))
        return subjects
    }

    private List<Subject> subjectQuery(Project project, boolean primary, XnatRecursionLevel recursion = XnatRecursionLevel.FULL_RECURSION) {
        final List<Subject> subjects = jsonQuery().queryParam('columns', 'label,project,gender,handedness,education,race,ethnicity,group,yob,dob,age,height,weight,src').
                get(projectSubjectsUrl(project)).jsonPath().getObject("ResultSet.Result.findAll { it.project ${primary ? '=' : '!'}= '${project.id}' }", Subject[])

        subjects.each { subject ->
            if (xnatInterface.readExtendedMetadata) {
                extractSubjectData(subject, readSubjectJsonPath(subject.accessionNumber))
            }
            if (xnatInterface.readResources) {
                subject.resources(xnatInterface.readResources(new SubjectResource().project(project).subject(subject)))
            }
            if (recursion.readSubjectAssessors) {
                subject.experiments(
                        xnatInterface.readSubjectAssessors(project, subject, recursion)
                )
            }
        }
        subjects
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

    protected JsonPath readSubjectJsonPath(String subjectId) {
        jsonQuery().get(formatRestUrl('subjects', subjectId)).jsonPath().setRootPath('items[0]')
    }

    protected void extractSubjectData(Subject subject, JsonPath subjectJsonPath) {
        subject.label(subjectJsonPath.getString('data_fields.label'))
        if (subjectJsonPath.get(FIELD_JSONPATH) != null) {
            subjectJsonPath.getList(FIELD_JSONPATH + '.items.data_fields').each { fieldObject ->
                subject.fields.put(fieldObject['name'], fieldObject['field'])
            }
        }
        if (subjectJsonPath.get(SHARING_JSONPATH) != null) {
            subjectJsonPath.getList(SHARING_JSONPATH + '.items.data_fields').each { shareObject ->
                subject.shares << new Share(shareObject['project'], shareObject['label'])
            }
        }
    }

    protected static String subfieldJsonpath(String fieldName) {
        "children.find { it.field == '${fieldName}' }"
    }

}
