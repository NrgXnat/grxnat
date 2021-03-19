package org.nrg.xnat.subinterfaces

import com.jayway.restassured.path.json.JsonPath
import groovyx.gpars.GParsPool
import org.nrg.xnat.enums.Accessibility
import org.nrg.xnat.enums.PrearchiveCode
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Investigator
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.extensions.project.ProjectQueryPutExtension
import org.nrg.xnat.pogo.resources.ProjectResource
import org.nrg.xnat.pogo.users.CustomUserGroup
import org.nrg.xnat.pogo.users.User
import org.nrg.xnat.pogo.users.UserGroup

import static com.jayway.restassured.http.ContentType.URLENC
import static org.nrg.xnat.enums.DataAccessLevel.*

class ProjectSubinterface extends XnatFunctionalitySubinterface {

    private static final String ALIAS_JSONPATH = childrenSubfieldJsonpath('aliases/alias')
    private static final String INVESTIGATOR_JSONPATH = childrenSubfieldJsonpath('investigators/investigator')
    private static final String PI_JSONPATH = childrenSubfieldJsonpath('PI')

    @Override
    List<String> getHandledEndpoints() {
        [
                '/projects',
                '/projects/{PROJECT_ID}',
                '/projects/{PROJECT_ID}/accessibility',
                '/projects/{PROJECT_ID}/accessibility/{ACCESS_LEVEL}',
                '/projects/{PROJECT_ID}/groups',
                '/projects/{PROJECT_ID}/users',
                '/projects/{PROJECT_ID}/users/{GROUP_ID}/{USER_ID}'
        ]
    }

    String accessibilityRestUrl(Project project) {
        formatRestUrl("/projects/${project.id}/accessibility")
    }

    String accessibilityRestUrl(Project project, Accessibility accessibility) {
        formatRestUrl("/projects/${project.id}/accessibility/${accessibility.toString()}")
    }

    void updateAccessibility(Project project, Accessibility accessibility) {
        queryBase().put(accessibilityRestUrl(project, accessibility)).then().assertThat().statusCode(200)
        project.accessibility(accessibility)
    }

    String projectUrl(Project project) {
        if (project.id == null) {
            throw new UnsupportedOperationException('project.id cannot be null')
        }
        formatRestUrl("projects/${project.id}")
    }

    void setPrearchiveSetting(Project project, PrearchiveCode code) {
        queryBase().put(formatRestUrl("/projects/${project.id}/prearchive_code/${code.code}")).then().assertThat().statusCode(200)
    }

    List<Project> listProjects() {
        jsonQuery().queryParam('columns', 'description,ID,secondary_ID,name,keywords').get(formatRestUrl('projects')).then().assertThat().statusCode(200).and().extract().jsonPath().getObject('ResultSet.Result', Project[])
    }

    Accessibility readAccessibility(Project project) {
        project.setAccessibility(
                queryBase().get(formatRestUrl("projects/${project}/accessibility")).then().assertThat().statusCode(200).and().extract().response().asString().trim()
        )
        project.accessibility
    }

    Project readProject(String projectID) {
        final JsonPath projectCall = jsonQuery().get(projectUrl(new Project(projectID))).then().
                assertThat().statusCode(200).and().extract().jsonPath().setRoot('items')

        final Project project = projectCall.getObject('data_fields.get(0)', Project).id(projectID)

        if (projectCall.get(ALIAS_JSONPATH) != null) {
            project.setAliases(
                    projectCall.getList("${ALIAS_JSONPATH}.items.data_fields.alias")
            )
        }

        if (projectCall.get(INVESTIGATOR_JSONPATH) != null) {
            project.setInvestigators(projectCall.getObject("${INVESTIGATOR_JSONPATH}.items.data_fields", Investigator[]) as List)
        }

        if (projectCall.get(PI_JSONPATH) != null) {
            project.setPi(projectCall.getObject("${PI_JSONPATH}.items.get(0).data_fields", Investigator))
        }

        readAccessibility(project)

        try {
            final List<User> existingUsers = xnatInterface.readSiteUsers()

            jsonQuery().get(formatRestUrl("/projects/${project}/users")).then().assertThat().statusCode(200).and().extract().jsonPath().getList('ResultSet.Result').each { userMap ->
                final User user = existingUsers.find { it.username == userMap['login'] }
                switch (userMap['GROUP_ID']) {
                    case "${project}_owner":
                        project.owners << user
                        break
                    case "${project}_member":
                        project.members << user
                        break
                    case "${project}_collaborator":
                        project.collaborators << user
                        break
                    default:
                        println "Unknown group name: ${userMap['GROUP_ID']}"
                }
            }
        } catch (Error ignored) {} // if we can't access user list, oh well

        if (xnatInterface.readResources) {
            project.resources(xnatInterface.readResources(new ProjectResource().project(project)))
        }

        project.subjects(xnatInterface.readSubjects(project))
        project.secondarySubjects(xnatInterface.readSecondarySubjects(project))
        // TODO: anon scripts
    }

    void createProject(Project project) {
        if (project == null) {
            throw new UnsupportedOperationException("project cannot be null")
        }

        if (project.extension == null) {
            project.extension(new ProjectQueryPutExtension(project))
        }

        xnatInterface.createInvestigators((project.pi != null) ? project.investigators + project.pi : project.investigators)

        project.extension.create(xnatInterface)

        xnatInterface.setupProjectEventSubscriptions(project)

        project.customUserGroups.each { group ->
            createCustomUserGroup(project, group)
        }

        if (project.prearchiveCode != null) {
            setPrearchiveSetting(project, project.prearchiveCode)
        }

        project.users.each { group, userList ->
            userList.each { user ->
                addUserToProject(user, project, group)
            }
        }

        project.projectResources.each { resource ->
            resource.project(project)
        }
        xnatInterface.uploadResources(project.projectResources)

        if (project.isSubjectParallelization()) {
            GParsPool.withPool {
                project.subjects.eachParallel { subject ->
                    //noinspection GroovyAssignabilityCheck
                    xnatInterface.createSubject(subject.project(project))
                }
            }
        } else {
            project.subjects.each { subject ->
                xnatInterface.createSubject(subject.project(project))
            }
        }
    }

    void addUserToProject(User addedUser, Project project, UserGroup userGroup) {
        queryBase().put(formatRestUrl("/projects/${project.id}/users/${project.id}_${userGroup.groupIdSuffix()}/${addedUser.username}"))
    }

    void createCustomUserGroup(Project project, CustomUserGroup userGroup) {
        final Map<String, Object> formData = [
                'xdat:userGroup/displayName' : userGroup.singularName(),
                'xdat:userGroup/tag' : project.id,
                'src' : 'project',
                'ELEMENT_0' : 'xdat:userGroup',
                'eventSubmit_doPerform' : 'Submit',
                ("xnat:projectData_xnat:projectData/ID_${project.id}_R".toString()): 1,
                (customUserGroupPermissionString(project, DataType.SUBJECT, 'R')) : 1
        ] as Map<String, Object>
        userGroup.accessLevelMap.each { dataType, level ->
            if (level in [READ_ONLY, CREATE_AND_EDIT, DELETE, ALL]) {
                formData.put(customUserGroupPermissionString(project, dataType, 'R'), 1)
            }
            if (level in [CREATE_AND_EDIT, ALL]) {
                formData.put(customUserGroupPermissionString(project, dataType, 'E'), 1)
            }
            if (level in [DELETE, ALL]) {
                formData.put(customUserGroupPermissionString(project, dataType, 'D'), 1)
            }
        }
        queryBase().contentType(URLENC).formParams(formData).post(formatRestUrl("projects/${project}/groups")).then().assertThat().statusCode(200)
    }

    void deleteProject(Project project) {
        queryBase().queryParam('removeFiles', true).delete(projectUrl(project)).then().assertThat().statusCode(200)
    }

    private String customUserGroupPermissionString(Project project, DataType dataType, String permission) {
        "${dataType.xsiType}_${dataType.xsiType}/project_${project}_${permission}"
    }

    private static String childrenSubfieldJsonpath(String fieldName) {
        "children.get(0).find { it.field == '${fieldName}' }"
    }

}
