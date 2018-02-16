package org.nrg.xnat.interfaces

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Optional
import com.jayway.restassured.RestAssured
import com.jayway.restassured.config.RestAssuredConfig
import com.jayway.restassured.mapper.factory.Jackson2ObjectMapperFactory
import com.jayway.restassured.path.json.JsonPath
import com.jayway.restassured.response.Response
import com.jayway.restassured.specification.RequestSpecification
import groovyx.gpars.GParsPool
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.time.StopWatch
import org.nrg.testing.CommonUtils
import org.nrg.xnat.enums.Accessibility
import org.nrg.xnat.enums.PrearchiveCode
import org.nrg.xnat.jackson.mappers.XnatRestReadWriteObjectMapper
import org.nrg.xnat.pogo.AnonScript
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Investigator
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Share
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.Experiment
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.NonimagingAssessor
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.pogo.experiments.SubjectAssessor
import org.nrg.xnat.pogo.extensions.SimpleResourceFileExtension
import org.nrg.xnat.pogo.extensions.project.ProjectQueryPutExtension
import org.nrg.xnat.pogo.extensions.session_assessor.SessionAssessorQueryPutExtension
import org.nrg.xnat.pogo.extensions.subject.SubjectQueryPutExtension
import org.nrg.xnat.pogo.extensions.subject_assessor.SessionImportExtension
import org.nrg.xnat.pogo.extensions.subject_assessor.SubjectAssessorQueryPutExtension
import org.nrg.xnat.pogo.resources.ProjectResource
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.pogo.resources.ResourceFile
import org.nrg.xnat.pogo.resources.ScanResource
import org.nrg.xnat.pogo.resources.SessionAssessorResource
import org.nrg.xnat.pogo.resources.SubjectAssessorResource
import org.nrg.xnat.pogo.resources.SubjectResource
import org.nrg.xnat.pogo.users.CustomUserGroup
import org.nrg.xnat.pogo.users.User
import org.nrg.xnat.pogo.users.UserGroup
import org.nrg.xnat.rest.SerializationUtils
import org.nrg.xnat.rest.XnatAliasToken
import org.nrg.xnat.rest.XnatSessionFilter

import static com.jayway.restassured.RestAssured.given
import static com.jayway.restassured.config.ObjectMapperConfig.objectMapperConfig
import static com.jayway.restassured.http.ContentType.JSON
import static com.jayway.restassured.http.ContentType.URLENC
import static org.nrg.xnat.enums.DataAccessLevel.*

abstract class XnatInterface {

    public static final ObjectMapper XNAT_REST_MAPPER = new XnatRestReadWriteObjectMapper()
    protected XnatSessionFilter sessionFilter
    protected String xnatUrl
    protected User authUser
    protected Optional<Boolean> isAdmin = Optional.absent()
    protected boolean readResources = true

    protected XnatInterface(XnatSessionFilter sessionFilter) {
        this.xnatUrl = sessionFilter.xnatUrl
        this.sessionFilter = sessionFilter
        authUser = sessionFilter.user
    }

    static XnatInterface authenticate(String xnatUrl, User user, boolean allowInsecureSSL = false) {
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(objectMapperConfig().jackson2ObjectMapperFactory(
                new Jackson2ObjectMapperFactory() {
                    @Override
                    ObjectMapper create(Class aClass, String s) {
                        XNAT_REST_MAPPER
                    }
                }
        ))
        if (allowInsecureSSL) RestAssured.useRelaxedHTTPSValidation()

        if (given().get(CommonUtils.formatUrl(xnatUrl, '/app/template/Login.vm')).statusCode == 200) {
            final XnatSessionFilter sessionFilter = new XnatSessionFilter(user, xnatUrl, allowInsecureSSL)
            if (given().filter(sessionFilter).get(CommonUtils.formatUrl(xnatUrl, '/data/auth')).statusCode == 200) {
                final Response oldResponse = given().filter(sessionFilter).get(CommonUtils.formatUrl(xnatUrl, '/data/version'))
                if (oldResponse.statusCode == 200 && !oldResponse.asString().contains('<!')) {
                    new XnatInterface_1_6(sessionFilter)
                } else {
                    final Response newResponse = given().filter(sessionFilter).get(CommonUtils.formatUrl(xnatUrl, '/xapi/siteConfig/buildInfo'))
                    if (newResponse.statusCode == 200 && newResponse.getContentType().contains('json')) {
                        final String version = newResponse.jsonPath().getString('version')
                        if (version.startsWith('1.7.0')) {
                            new XnatInterface_1_7_0(sessionFilter)
                        } else if (version.startsWith('1.7.1')) {
                            new XnatInterface_1_7_1(sessionFilter)
                        } else if (version.startsWith('1.7.2')) {
                            new XnatInterface_1_7_2(sessionFilter)
                        } else if (version.startsWith('1.7.3')) {
                            new XnatInterface_1_7_3(sessionFilter)
                        } else if (version.startsWith('1.7.4')) {
                            new XnatInterface_1_7_4(sessionFilter)
                        } else {
                            new XnatInterface_1_7_5(sessionFilter)
                        }
                    } else {
                        new XnatInterface_1_7_5(sessionFilter)
                    }
                }
            } else {
                throw new AssertionError("Provided credentials don't appear to be valid.")
            }
        } else {
            throw new AssertionError("There doesn't seem to be an XNAT reachable at that address.")
        }
    }

    void disableResourceReading() {
        readResources = false
    }

    void enableResourceReading() {
        readResources = true
    }

    void logout() {
        queryBase().delete(formatRestUrl("/JSESSION/${sessionFilter.sessionId}")).then().assertThat().statusCode(200)
        sessionFilter.deleteSessionId()
    }

    void reauthenticate() {
        sessionFilter.extractSessionId()
    }

    void invalidateCachedUserSession() {
        logout()
        reauthenticate()
    }

    static XnatInterface authenticate(String xnatUrl, String username, String password, boolean allowInsecureSSL = false) {
        authenticate(xnatUrl, new User(username).password(password), allowInsecureSSL)
    }

    void setServerIssueRetryCount(int count) {
        sessionFilter.setServerIssueRetryCount(count)
    }

    String formatXnatUrl(String... components) {
        CommonUtils.formatUrl(xnatUrl, CommonUtils.formatUrl((Object[]) components))
    }

    String formatRestUrl(String... components) {
        formatXnatUrl('data', CommonUtils.formatUrl((Object[]) components))
    }

    String formatXapiUrl(String... components) {
        formatXnatUrl('xapi', CommonUtils.formatUrl((Object[]) components))
    }

    String readXnatCsrfToken() {
        final String csrfTokenLine = queryBase().get(formatXnatUrl('/app/Index.vm')).then().assertThat().statusCode(200).and().extract().response().asString().split('\n').find { it.contains('var csrfToken') }
        csrfTokenLine.substring(csrfTokenLine.indexOf("'") + 1, csrfTokenLine.lastIndexOf("'"))
    }

    void saveBinaryResponseToFile(Response response, File file) {
        response.then().assertThat().statusCode(200)
        final InputStream inputStream = response.asInputStream()
        try {
            FileUtils.copyInputStreamToFile(inputStream, file)
        } catch (IOException ioe) {
            throw new RuntimeException("Could not download data and copy to file: " + ioe)
        }
    }

    RequestSpecification queryBase() {
        given().filter(sessionFilter)
    }

    RequestSpecification jsonQuery() {
        queryBase().queryParam('format', 'json')
    }

    RequestSpecification requestWithCsrfToken() {
        queryBase().queryParam('XNAT_CSRF', readXnatCsrfToken())
    }

    protected void prohibitNonadmin() {
        if (!userIsAdmin()) throw new UnsupportedOperationException('You must be an admin to perform this operation.')
    }

    protected void notSupported() {
        throw new UnsupportedOperationException('REST call not supported in this version of XNAT.')
    }

    boolean queryUserAdmin() {
        final Response response = queryBase().get(formatXapiUrl("/users/${authUser.username}/roles"))
        if (response.statusCode == 200 && 'Administrator' in response.jsonPath().getList('')) {
            true
        } else {
            false
        }
    }

    final boolean userIsAdmin() {
        if (!isAdmin.isPresent()) {
            isAdmin = Optional.of(queryUserAdmin())
        }
        isAdmin.get()
    }

    void disableAdminCheck() {
        isAdmin = Optional.of(true)
    }

    List<User> readSiteUsers() {
        final Response response = jsonQuery().get(formatRestUrl('users'))

        if (response.statusCode == 403) {
            throw new AssertionError('This XNAT requires administrator privileges to read the list of site users.')
        }

        response.jsonPath().getObject('ResultSet.Result', User[]) as List
    }

    String jsessionId() {
        sessionFilter.sessionId
    }

    String issueAliasTokenUrl() {
        formatRestUrl('/services/tokens/issue')
    }

    XnatAliasToken generateAliasToken() {
        queryBase().get(issueAliasTokenUrl()).then().assertThat().statusCode(200).and().extract().jsonPath().getObject('', XnatAliasToken)
    }

    String projectExperimentsUrl(Project project) {
        formatRestUrl("projects/${project.id}/experiments")
    }

    String getAccessionNumber(Subject subject) {
        if (subject.project == null) throw new IllegalArgumentException('subject must have project specified')

        subject.accessionNumber ?: subject.accessionNumber(jsonQuery().get(projectSubjectsUrl(subject.project)).jsonPath().getString("ResultSet.Result.find { it.label == '${subject.label}' }.ID")).accessionNumber
    }

    String getAccessionNumber(Project project, SubjectAssessor subjectAssessor) {
        subjectAssessor.accessionNumber ?: subjectAssessor.accessionNumber(queryBase().get(projectExperimentsUrl(project)).then().extract().jsonPath().getString("ResultSet.Result.find {it.label == '${subjectAssessor}' }.ID")).accessionNumber
    }

    String getAccessionNumber(SubjectAssessor subjectAssessor) {
        if (subjectAssessor.primaryProject == null) throw new IllegalArgumentException("subjectAssessor object must have project specified.")
        getAccessionNumber(subjectAssessor.primaryProject, subjectAssessor)
    }

    Subject readSubject(String accessionNumber) {
        final Response response = jsonQuery().get(formatRestUrl('subjects', accessionNumber))
        final Subject subject = response.jsonPath().getObject("items.get(0).children.find { it.field == 'demographics' }.items.get(0).data_fields", Subject.class)
        subject.label(response.jsonPath().getString('items.get(0).data_fields.label'))
    }

    def <T extends Experiment> T readExperiment(String accessionNumber, Class<T> experimentClass) {
        final Response response = jsonQuery().get(formatRestUrl('experiments', accessionNumber))
        final T experiment = response.jsonPath().getObject('items.get(0).data_fields', experimentClass)
        experiment.dataType(DataType.lookup(response.jsonPath().getString("items.get(0).meta.'xsi:type'")))
    }

    void waitForAutoRun(ImagingSession session, int maxTimeInSeconds = 60) {
        println "A subsequent operation requires that the AutoRun pipeline complete on this session (${session}) before continuing. If this step appears to be hanging, it likely means that the pipeline engine is not configured correctly on the XNAT server. Waiting for AutoRun completion for up to ${maxTimeInSeconds} seconds..."
        final String accessionNumber = session.accessionNumber ?: getAccessionNumber(session)

        final StopWatch stopWatch = CommonUtils.launchStopWatch()
        while (true) {
            CommonUtils.checkStopWatch(stopWatch, maxTimeInSeconds, "AutoRun did not complete in allotted number of seconds: ${maxTimeInSeconds}")

            final String status = queryBase().queryParam("experiment", accessionNumber).queryParam("format", "json").
                    get(formatRestUrl('services/workflows/AutoRun')).then().extract().jsonPath().getString('items.get(0).data_fields.status')

            if (status == 'Complete') {
                return
            } else if (status == 'Failed') {
                throw new AssertionError('AutoRun failed.')
            }
            CommonUtils.sleep(1000)
        }
    }

    String getBuildInfo() {
        final JsonPath buildPath = queryBase().get(formatXapiUrl('/siteConfig/buildInfo')).then().assertThat().statusCode(200).and().extract().jsonPath()
        "Version ${buildPath.get('version')} (commit ${buildPath.get('commit')})"
    }

    void uploadToSessionZipImporter(File sessionZip, Project project, Subject subject, ImagingSession session) {
        if (project == null) {
            throw new IllegalArgumentException('Project cannot be null when uploading to zip importer.')
        }

        final Map<String, String> queryPararms = [:]
        queryPararms.put('dest', '/archive')
        queryPararms.put('PROJECT_ID', project.getId())

        if (subject != null) queryPararms.put('SUBJECT_ID', subject.label)
        if (session != null) queryPararms.put('EXPT_LABEL', session.label)

        queryBase().multiPart(sessionZip).queryParameters(queryPararms).when().post(formatRestUrl('/services/import')).then().assertThat().statusCode(200)
        getAccessionNumber(project, session)
    }

    void uploadToSessionZipImporter(File sessionZip, Project project) {
        uploadToSessionZipImporter(sessionZip, project, null, null)
    }

    void uploadToSessionZipImporter(File sessionZip, ImagingSession session) {
        if (session.primaryProject == null) throw new IllegalArgumentException('Session must have project object specified to use this shortcut method')
        uploadToSessionZipImporter(sessionZip, session.primaryProject, session.subject, session)
    }

    String userSessionsRestUrl(User user) {
        formatRestUrl("user/${user.username}/sessions")
    }

    void expireAllActiveSessions(User targetUser) {
        if (userIsAdmin() || authUser == targetUser) {
            queryBase().delete(userSessionsRestUrl(targetUser)).then().assertThat().statusCode(200)
        } else {
            prohibitNonadmin()
        }
    }

    void expireAllActiveSessions() {
        expireAllActiveSessions(authUser)
    }

    int getNumberActiveSessions(User targetUser) {
        if (userIsAdmin() || authUser == targetUser) {
            queryBase().get(userSessionsRestUrl(targetUser)).then().assertThat().statusCode(200).assertThat().extract().jsonPath().getInt(targetUser.username)
        } else {
            prohibitNonadmin()
        }
    }

    int getNumberActiveSessions() {
        getNumberActiveSessions(authUser)
    }

    void createUser(User user) {
        prohibitNonadmin()
        queryBase().contentType(JSON).body(user).post(formatXapiUrl('users')).then().assertThat().statusCode(201)
    }

    void verifyUser(User user) {
        prohibitNonadmin()
        queryBase().put(formatXapiUrl("/users/${user.username}/verified/true")).then().assertThat().statusCode(200)
        user.verified(true)
    }

    void enableUser(User user) {
        prohibitNonadmin()
        queryBase().put(formatXapiUrl("/users/${user.username}/enabled/true")).then().assertThat().statusCode(200)
        user.enabled(true)
    }

    void assignUserToRoles(User user, String... roles) {
        prohibitNonadmin()
        queryBase().contentType(JSON).body(roles).put(formatXapiUrl("/users/${user.username}/roles")).then().assertThat().statusCode(200)
    }

    void addUserToGroups(User user, String... groups) {
        queryBase().contentType(JSON).body(groups).put(formatXapiUrl("/users/${user.username}/groups")).then().assertThat().statusCode(200)
    }

    void makeUserAdmin(User user) {
        assignUserToRoles(user, 'Administrator')
        addUserToGroups(user, 'ALL_DATA_ADMIN')
        user.admin(true)
    }

    void postToSiteConfig(Map configSettings) {
        prohibitNonadmin()
        queryBase().contentType(JSON).body(configSettings).post(formatXapiUrl('siteConfig')).then().assertThat().statusCode(200)
    }

    void setLoginRequirement(boolean loginRequired) {
        postToSiteConfig(['requireLogin' : loginRequired])
    }

    void openXnat() {
        setLoginRequirement(false)
    }

    void closeXnat() {
        setLoginRequirement(true)
    }

    private AnonScript readAnonScript(Response response) {
        new AnonScript().contents(
                response.then().assertThat().statusCode(200).and().extract().jsonPath().getString("ResultSet.Result.get(0).script")
        )
    }

    String legacySiteAnonScriptUrl() {
        formatRestUrl('config/edit/image/dicom/script')
    }

    AnonScript readSiteAnonScript() {
        readAnonScript(jsonQuery().get(legacySiteAnonScriptUrl()))
    }

    void setSiteAnonScriptStatus(boolean status) {
        postToSiteConfig(['enableSitewideAnonymizationScript' : status])
    }

    void disableSiteAnonScript() {
        setSiteAnonScriptStatus(false)
    }

    void enableSiteAnonScript() {
        setSiteAnonScriptStatus(true)
    }

    void setSiteAnonScript(AnonScript script) {
        postToSiteConfig(['sitewideAnonymizationScript' : script.getContents()])
    }

    String projectAnonScriptUrlBase(Project project) {
        formatRestUrl("config/edit/projects/${project.id}/image/dicom")
    }

    String projectAnonScriptUrl(Project project) {
        "${projectAnonScriptUrlBase(project)}/script"
    }

    AnonScript readProjectAnonScript(Project project) {
        readAnonScript(jsonQuery().get(projectAnonScriptUrl(project)))
    }

    void setProjectAnonScript(Project project, AnonScript script) {
        queryBase().body(script.getContents()).put(projectAnonScriptUrl(project)).then().assertThat().statusCode(200)
    }

    void setProjectAnonScriptStatus(Project project, boolean status) {
        queryBase().queryParam('activate', status).put("${projectAnonScriptUrlBase(project)}/status").then().assertThat().statusCode(200)
    }

    void disableProjectAnonScript(Project project) {
        setProjectAnonScriptStatus(project, false)
    }

    void enableProjectAnonScript(Project project) {
        setProjectAnonScriptStatus(project, true)
    }

    List<Investigator> readInvestigators() {
        queryBase().get(formatXapiUrl('investigators')).as(Investigator[].class) as List<Investigator>
    }

    void createInvestigators(List<Investigator> investigators) {
        final List<Investigator> knownInvestigators = readInvestigators()

        investigators.each { investigator ->
            final Investigator extant = knownInvestigators.find { it == investigator }
            if (extant == null) {
                createInvestigator(investigator)
            } else {
                investigator.id(extant.getXnatInvestigatordataId())
            }
        }
    }

    void createInvestigator(Investigator investigator) {
        investigator.setXnatInvestigatordataId(
                queryBase().contentType(JSON).body(investigator).post(formatXapiUrl('investigators')).jsonPath().getInt('xnatInvestigatordataId')
        )
    }

    void addListedUsersToProject(Project project) {
        project.users.each { group, userList ->
            userList.each { user ->
                addUserToProject(user, project, group)
            }
        }
    }

    void addUserToProject(User addedUser, Project project, UserGroup userGroup) {
        addUserToGroups(addedUser, "${project.id}_${(userGroup instanceof CustomUserGroup) ? userGroup.name : userGroup.singularName().toLowerCase()}")
    }

    void createCustomUserGroup(Project project, CustomUserGroup userGroup) {
        final Map<String, Object> formData = ['xdat:userGroup/displayName' : userGroup.singularName(), 'xdat:userGroup/tag' : project.id, 'src' : 'project', 'ELEMENT_0' : 'xdat:userGroup', 'eventSubmit_doPerform' : 'Submit', "xnat:projectData_xnat:projectData/ID_${project.id}_R" : 1, (customUserGroupPermissionString(project, DataType.SUBJECT, 'R')) : 1]
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

    String customUserGroupPermissionString(Project project, DataType dataType, String permission) {
        "${dataType.xsiType}_${dataType.xsiType}/project_${project}_${permission}"
    }

    void uploadResources(List<Resource> resources) {
        resources.each { resource ->
            uploadResource(resource)
        }
    }

    void uploadResource(Resource resource) {
        queryBase().queryParams(SerializationUtils.serializeToMap(resource)).put(formatXnatUrl("${resource.resourceUrl()}/resources/${resource.folder}")).then().assertThat().statusCode(200)

        resource.resourceFiles.each { file ->
            if (file.extension == null) {
                final File possibleFile = new File(file.getName())
                if (possibleFile != null && possibleFile.exists() && possibleFile.isFile()) {
                    file.extension(new SimpleResourceFileExtension(file, possibleFile))
                } else {
                    throw new UnsupportedOperationException('ResourceFile must have extension set in order to locate file for upload.')
                }
            }
            file.extension.xnatInterface(this).uploadTo(resource)
        }

        final Resource responseResource = jsonQuery().get(formatXnatUrl(resource.resourceUrl(), 'resources')).then().assertThat().statusCode(200).
                and().extract().response().jsonPath().getObject("ResultSet.Result.find { it.label == '${resource.folder}' }", resource.class)

        resource.fileCount(responseResource.fileCount).fileSize(responseResource.fileSize)
    }

    String resourceFilesUrl(Resource resource) {
        formatXnatUrl("${resource.resourceUrl()}/resources/${resource.folder}/files")
    }

    String resourceFileUrl(Resource resource, ResourceFile file) {
        CommonUtils.formatUrl(resourceFilesUrl(resource), file.name)
    }

    /**
     * Reads the list of Resource objects at the XNAT-level specified in the dummyResource
     * @param dummyResource
     * @return
     */
    List<Resource> readResources(Resource dummyResource) {
        final List resourceResp = jsonQuery().get(formatXnatUrl(dummyResource.resourceUrl(), 'resources')).then().assertThat().statusCode(200).
            and().extract().response().jsonPath().getList('ResultSet.Result')
        final List<Resource> resources = SerializationUtils.deserializeList(resourceResp, dummyResource.class)
        resources.each { resource ->
            if (dummyResource.project != null) resource.project(dummyResource.project)
            if (dummyResource.subject != null) resource.subject(dummyResource.subject)
            if (dummyResource.subjectAssessor != null) resource.subjectAssessor(dummyResource.subjectAssessor)
            if (dummyResource.scan != null) resource.scan(dummyResource.scan)
            if (dummyResource.sessionAssessor != null) resource.sessionAssessor(dummyResource.sessionAssessor)
            readResourceFiles(resource)
        }
        resources
    }

    List<ResourceFile> readResourceFiles(Resource resource) {
        resource.resourceFiles(jsonQuery().get(formatXnatUrl("${resource.resourceUrl()}/resources/${resource.folder}/files")).jsonPath().getObject('ResultSet.Result', ResourceFile[]) as List<ResourceFile>).resourceFiles
    }

    void deleteResource(Resource resource) {
        queryBase().queryParam('removeFiles', true).delete(formatXnatUrl(resource.resourceUrl(), "resources/${resource.folder}")).then().assertThat().statusCode(200)
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
        formatRestUrl("projects/${project.id}")
    }

    void setPrearchiveSetting(Project project, PrearchiveCode code) {
        queryBase().put(formatRestUrl("/projects/${project.id}/prearchive_code/${code.code}")).then().assertThat().statusCode(200)
    }

    Project readProject(String projectID) {
        final JsonPath projectCall = jsonQuery().get(projectUrl(new Project(projectID))).then().
                assertThat().statusCode(200).and().extract().jsonPath().setRoot('items')

        final Project project = projectCall.getObject('data_fields.get(0)', Project).id(projectID)

        if (projectCall.get("children.get(0).find { it.field == 'aliases/alias' }") != null) {
            project.setAliases(
                    projectCall.getList("children.get(0).find { it.field == 'aliases/alias' }.items.data_fields.alias")
            )
        }

        if (projectCall.get("children.get(0).find { it.field == 'investigators/investigator' }.") != null) {
            project.setInvestigators(projectCall.getObject("children.get(0).find { it.field == 'investigators/investigator' }.items.data_fields", Investigator[]) as List)
        }

        if (projectCall.get("children.get(0).find { it.field == 'PI' }") != null) {
            project.setPi(projectCall.getObject("children.get(0).find { it.field == 'PI' }.items.get(0).data_fields", Investigator))
        }

        project.setAccessibility(
                queryBase().get(formatRestUrl("projects/${project}/accessibility")).then().assertThat().statusCode(200).and().extract().response().asString().trim()
        )

        try {
            final List<User> existingUsers = readSiteUsers()

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

        if (readResources) {
            project.resources(readResources(new ProjectResource().project(project)))
        }

        project.subjects(readSubjects(project))
        // TODO: anon scripts
    }

    List<Subject> readSubjects(Project project) {
        final List<Subject> subjects = jsonQuery().queryParam('columns', 'label,project,gender,handedness,education,race,ethnicity,group,yob,dob,age,height,weight,src').
            get(projectSubjectsUrl(project)).jsonPath().getObject("ResultSet.Result.findAll { it.project == '${project.id}' }", Subject[])

        subjects.each { subject ->
            if (readResources) {
                subject.resources(readResources(new SubjectResource().project(project).subject(subject)))
            }
            subject.experiments(
                readSubjectAssessors(project, subject)
            )
        }
        subjects
        // TODO: shares, secondary subjects
    }

    List<SubjectAssessor> readSubjectAssessors(Project project, Subject subject) {
        List imagingMaps, nonimagingMaps

        (imagingMaps, nonimagingMaps) = jsonQuery().queryParam("columns", "note,date,label,ID").
                get(formatRestUrl("/projects/${project}/subjects/${subject}/experiments")).then().assertThat().statusCode(200).
                and().extract().response().jsonPath().getList('ResultSet.Result').split { it.xsiType.matches('xnat:.+SessionData') }

        final List<SubjectAssessor> nonimagingSubjectAssessors = SerializationUtils.deserializeList(nonimagingMaps, NonimagingAssessor)
        final List<ImagingSession> imagingSessions = SerializationUtils.deserializeList(imagingMaps, ImagingSession)

        subject.experiments(nonimagingSubjectAssessors + imagingSessions)

        subject.experiments.each { assessor ->
            if (readResources) {
                assessor.resources(readResources(new SubjectAssessorResource().project(project).subject(subject).subjectAssessor(assessor)))
            }
        }

        imagingSessions.each { session ->
            session.scans(readScans(project, subject, session))
            session.assessors(readSessionAssessors(project, subject, session))
        }
    }

    List<Scan> readScans(Project project, Subject subject, ImagingSession session) {
        jsonQuery().get(sessionScansUrl(project, subject, session)).jsonPath().getObject('ResultSet.Result', Scan[]).collect { scan ->
            if (readResources) {
                scan.scanResources(readResources(new ScanResource().project(project).subject(subject).subjectAssessor(session).scan(scan)))
            }
            scan.session(session)
        }
    }

    List<SessionAssessor> readSessionAssessors(Project project, Subject subject, ImagingSession session) {
        jsonQuery().get(assessorsUrl(project, subject, session)).jsonPath().getObject('ResultSet.Result', SessionAssessor[]).collect { assessor ->
            if (readResources) {
                assessor.resources(readResources(new SessionAssessorResource().project(project).subject(subject).subjectAssessor(session).sessionAssessor(assessor)))
            }
            assessor as SessionAssessor
        }
    }

    void populateAdditionalScanMetadata(Project project, Subject subject, ImagingSession session) {
        session.scans.each { scan ->
            readAdditionalScanMetadata(project, subject, session, scan)
        }
    }

    Scan readAdditionalScanMetadata(Project project, Subject subject, ImagingSession session, Scan scan) {
        scan.uid(jsonQuery().get(scanUrl(project, subject, session, scan)).jsonPath().getString('items[0].data_fields.UID'))
    }

    Resource findResource(List<Resource> resources, String resourceLabel) {
        resources.find { it.folder == resourceLabel }
    }

    void createProject(Project project) {
        if (project == null) {
            throw new UnsupportedOperationException("project cannot be null")
        }

        if (project.extension == null) {
            project.extension(new ProjectQueryPutExtension(this, project))
        }

        createInvestigators((project.pi != null) ? project.investigators + project.pi : project.investigators)

        project.extension.create()

        project.customUserGroups.each { group ->
            createCustomUserGroup(project, group)
        }

        if (project.prearchiveCode != null) setPrearchiveSetting(project, project.prearchiveCode)

        addListedUsersToProject(project)

        project.projectResources.each { resource ->
            resource.project(project)
        }
        uploadResources(project.projectResources)

        if (project.isSubjectParallelization()) {
            GParsPool.withPool {
                project.subjects.eachParallel { subject ->
                    //noinspection GroovyAssignabilityCheck
                    createSubject(subject.project(project))
                }
            }
        } else {
            project.subjects.each { subject ->
                createSubject(subject.project(project))
            }
        }
    }

    void createSubject(Project project, Subject subject) {
        if (project == null) {
            throw new UnsupportedOperationException('project cannot be null')
        }
        if (subject == null) {
            throw new UnsupportedOperationException('subject cannot be null')
        }

        if (subject.extension == null) {
            subject.extension(new SubjectQueryPutExtension(this, subject))
        }

        subject.extension.create(project)

        subject.resources.each { resource ->
            resource.project(project).subject(subject)
        }
        uploadResources(subject.resources)

        subject.shares.each { share ->
            shareSubject(project, subject, share)
        }

        if (project.isSubjectAssessorParallelization()) {
            GParsPool.withPool {
                subject.experiments.eachParallel { subjectAssessor ->
                    createSubjectAssessor(project, subject, subjectAssessor as SubjectAssessor)
                }
            }
        } else {
            subject.experiments.each { subjectAssessor ->
                createSubjectAssessor(project, subject, subjectAssessor)
            }
        }
    }

    String projectSubjectsUrl(Project project) {
        formatRestUrl("/projects/${project.id}/subjects")
    }

    String subjectUrl(Project project, Subject subject) {
        formatRestUrl("/projects/${project.id}/subjects/${subject.label}")
    }

    String subjectUrl(Subject subject) {
        if (subject.project == null) {
            throw new UnsupportedOperationException('Subject must have project object set.')
        }
        subjectUrl(subject.project, subject)
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

        queryBase().queryParam('label', share.destinationLabel ?: subject.label).put(CommonUtils.formatUrl(subjectUrl(sourceProject, subject), "projects/${share.destinationProject}")).
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
        if (project == null) {
            throw new UnsupportedOperationException('project cannot be null')
        }
        if (subject == null) {
            throw new UnsupportedOperationException('subject cannot be null')
        }

        queryBase().delete(subjectUrl(project, subject)).then().assertThat().statusCode(200)
    }

    void deleteSubject(Subject subject) {
        deleteSubject(subject.project, subject)
    }

    void createSubjectAssessor(Project project, Subject subject, SubjectAssessor subjectAssessor) {
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
            subjectAssessor.extension(new SubjectAssessorQueryPutExtension(this, subjectAssessor))
        }
        subjectAssessor.extension.create(project, subject)

        subjectAssessor.resources.each { resource ->
            resource.project(project).subject(subject).subjectAssessor(subjectAssessor)
        }
        uploadResources(subjectAssessor.resources)

        subjectAssessor.shares.each { share ->
            shareSubjectAssessor(project, subject, subjectAssessor, share)
        }

        if (subjectAssessor instanceof ImagingSession) {
            final ImagingSession session = subjectAssessor as ImagingSession
            if (session.scans.size() + session.assessors.size() > 0 && session.extension instanceof SessionImportExtension) waitForAutoRun(session, 600)

            if (project.isScanParallelization()) {
                GParsPool.withPool {
                    session.scans.eachParallel { scan ->
                        createScan(project, subject, session, scan as Scan)
                    }
                }
            } else {
                session.scans.each { scan ->
                    createScan(project, subject, session, scan)
                }
            }

            if (project.isSessionAssessorParallelization()) {
                GParsPool.withPool {
                    session.assessors.each { assessor ->
                        createSessionAssessor(project, subject, session, assessor as SessionAssessor)
                    }
                }
            } else {
                session.assessors.each { assessor ->
                    createSessionAssessor(project, subject, session, assessor)
                }
            }
        }
    }

    void createSubjectAssessor(SubjectAssessor subjectAssessor) {
        createSubjectAssessor(subjectAssessor.getPrimaryProject(), subjectAssessor.getSubject(), subjectAssessor)
    }

    void shareSubjectAssessor(Project project, Subject subject, SubjectAssessor subjectAssessor, Share share) {
        if (share.destinationProject == null) {
            throw new UnsupportedOperationException('Destination project string cannot be null for experiment sharing.')
        }

        queryBase().queryParam('label', share.destinationLabel ?: subjectAssessor.label).
                put(CommonUtils.formatUrl(subjectAssessorUrl(project, subject, subjectAssessor), 'projects', share.destinationProject)).then().assertThat().statusCode(200)
    }

    void relabelSubjectAssessor(Project project, Subject subject, SubjectAssessor subjectAssessor, String newLabel) {
        queryBase().queryParam('label', newLabel).put(subjectAssessorUrl(project, subject, subjectAssessor)).then().assertThat().statusCode(200)
        subjectAssessor.setLabel(newLabel)
    }

    void relabelSubjectAssessor(SubjectAssessor subjectAssessor, String newLabel) {
        relabelSubjectAssessor(subjectAssessor.primaryProject, subjectAssessor.subject, subjectAssessor, newLabel)
    }

    void reassignSubjectAssessor(SubjectAssessor experiment, Subject destinationSubject) {
        final Subject originalSubject = experiment.subject
        queryBase().put(formatRestUrl("/projects/${experiment.primaryProject}/subjects/${destinationSubject.label}/experiments/${getAccessionNumber(experiment)}")).then().assertThat().statusCode(200)
        originalSubject.removeExperiment(experiment)
        destinationSubject.addExperiment(experiment)
    }

    void deleteSubjectAssessor(Project project, Subject subject, SubjectAssessor subjectAssessor) {
        if (project == null) {
            throw new UnsupportedOperationException("project cannot be null")
        }
        if (subject == null) {
            throw new UnsupportedOperationException("subject cannot be null")
        }
        if (subjectAssessor == null) {
            throw new UnsupportedOperationException("subjectAssessor cannot be null")
        }

        queryBase().queryParam('removeFiles', true).delete(subjectAssessorUrl(project, subject, subjectAssessor)).then().assertThat().statusCode(200)
    }

    void deleteSubjectAssessor(SubjectAssessor subjectAssessor) {
        deleteSubjectAssessor(subjectAssessor.getPrimaryProject(), subjectAssessor.getSubject(), subjectAssessor)
    }

    String subjectAssessorUrl(Project project, Subject subject, SubjectAssessor assessor) {
        formatRestUrl("projects/${project.id}/subjects/${subject.label}/experiments/${assessor.label}")
    }

    String subjectAssessorUrl(SubjectAssessor assessor) {
        subjectAssessorUrl(assessor.primaryProject ?: assessor.subject.project, assessor.subject, assessor)
    }

    String sessionScansUrl(Project project, Subject subject, ImagingSession session) {
        formatRestUrl("projects/${project.id}/subjects/${subject.label}/experiments/${session.label}/scans")
    }

    String sessionScansUrl(ImagingSession session) {
        sessionScansUrl(session.getPrimaryProject(), session.getSubject(), session)
    }

    void createScan(Project project, Subject subject, ImagingSession session, Scan scan) {
        if (scan.xsiType == null) {
            throw new UnsupportedOperationException("scan must have an xsiType")
        }
        queryBase().queryParams(SerializationUtils.serializeToMap(scan)).put(scanUrl(project, subject, session, scan)).then().assertThat().statusCode(200)

        scan.scanResources.each { resource ->
            resource.project(project).subject(subject).subjectAssessor(session).scan(scan)
        }
        uploadResources(scan.scanResources)
    }

    String scanUrl(Project project, Subject subject, ImagingSession session, Scan scan) {
        CommonUtils.formatUrl(sessionScansUrl(project, subject, session), scan.id)
    }

    String scanUrl(Scan scan) {
        scanUrl(scan.getSession().getPrimaryProject(), scan.getSession().getSubject(), scan.getSession(), scan)
    }

    void updateScan(Project project, Subject subject, ImagingSession session, Scan scan) {
        queryBase().queryParams(SerializationUtils.serializeToMap(scan)).put(scanUrl(project, subject, session, scan)).then().assertThat().statusCode(200)
    }

    void updateScan(Scan scan) {
        updateScan(scan.session.primaryProject, scan.session.subject, scan.session, scan)
    }

    void deleteScan(Project project, Subject subject, ImagingSession session, Scan scan) {
        queryBase().queryParam("removeFiles", true).delete(scanUrl(project, subject, session, scan)).then().assertThat().statusCode(200)
    }

    void deleteScan(Scan scan) {
        queryBase().queryParam("removeFiles", true).delete(scanUrl(scan)).then().assertThat().statusCode(200)
    }

    String assessorsUrl(Project project, Subject subject, ImagingSession session) {
        formatRestUrl("projects/${project.id}/subjects/${subject.label}/experiments/${session.label}/assessors")
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
            assessor.extension(new SessionAssessorQueryPutExtension(this, assessor))
        }

        assessor.extension.create(project, subject, session)

        assessor.resources.each { resource ->
            resource.project(project).subject(subject).subjectAssessor(session).sessionAssessor(assessor)
        }
        uploadResources(assessor.resources)
    }

    void createSessionAssessor(SessionAssessor assessor) {
        createSessionAssessor(assessor.getPrimaryProject(), assessor.getSubject(), assessor.getParentSession(), assessor)
    }

    void deleteSessionAssessor(Project project, Subject subject, ImagingSession session, SessionAssessor sessionAssessor) {
        if (project == null) {
            throw new UnsupportedOperationException("project cannot be null")
        }
        if (subject == null) {
            throw new UnsupportedOperationException("subject cannot be null")
        }
        if (session == null) {
            throw new UnsupportedOperationException("session cannot be null")
        }
        if (sessionAssessor == null) {
            throw new UnsupportedOperationException("sessionAssessor cannot be null")
        }

        queryBase().delete(sessionAssessorUrl(project, subject, session, sessionAssessor)).then().assertThat().statusCode(200)
    }

    void deleteSessionAssessor(SessionAssessor sessionAssessor) {
        deleteSessionAssessor(sessionAssessor.getPrimaryProject(), sessionAssessor.getSubject(), sessionAssessor.getParentSession(), sessionAssessor)
    }

    String sessionAssessorUrl(Project project, Subject subject, ImagingSession session, SessionAssessor sessionAssessor) {
        formatRestUrl("projects/${project.id}/subjects/${subject.label}/experiments/${session.label}/assessors/${sessionAssessor.label}")
    }

    String sessionAssessorUrl(SessionAssessor assessor) {
        sessionAssessorUrl(assessor.getPrimaryProject(), assessor.getSubject(), assessor.getParentSession(), assessor)
    }

    void deleteProject(Project project) {
        queryBase().queryParam('removeFiles', true).delete(projectUrl(project)).then().assertThat().statusCode(200)
    }

    void deleteAllProjectData(Project project) {
        jsonQuery().queryParam('columns', 'ID,subject_ID').get(formatRestUrl("/projects/${project.id}/experiments")).jsonPath().getList('ResultSet.Result').each { experiment ->
            queryBase().queryParam('removeFiles', true).
                    delete(formatRestUrl("projects/${project.id}/subjects/${experiment.subject_ID}/experiments/${experiment.ID}")).
                    then().assertThat().statusCode(200)
        }

        jsonQuery().queryParam('columns', 'ID').get(formatRestUrl("projects/${project.id}/subjects")).jsonPath().getList('ResultSet.Result.ID').each { subject ->
            queryBase().queryParam('removeFiles', true).delete(formatRestUrl("projects/${project.id}/subjects/${subject}")).then().assertThat().statusCode(200)
        }

        final Project shadowProject = new Project(project.id)
        readResources(new ProjectResource().project(shadowProject)).each { resource ->
            deleteResource(resource)
        }
    }

    String assessorsUrlByAccessionNumber(ImagingSession session) {
        if (session.accessionNumber == null) {
            throw new UnsupportedOperationException('Method requires session object to have accessionNumber populated')
        }

        formatRestUrl("experiments/${session.accessionNumber}/assessors")
    }

    String assessorUrlByAccessionNumber(ImagingSession session, SessionAssessor assessor) {
        if (session.getAccessionNumber() == null) {
            throw new UnsupportedOperationException("Method requires session object to have accessionNumber populated.")
        }
        if (assessor.getAccessionNumber() == null) {
            throw new UnsupportedOperationException("Method requires assessor object to have accessionNumber populated.")
        }

        formatRestUrl("experiments/${session.accessionNumber}/assessors/${assessor.accessionNumber}")
    }

}
