package org.nrg.xnat.interfaces

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Optional
import com.jayway.restassured.RestAssured
import com.jayway.restassured.config.RestAssuredConfig
import com.jayway.restassured.http.ContentType
import com.jayway.restassured.internal.RestAssuredResponseImpl
import com.jayway.restassured.mapper.factory.Jackson2ObjectMapperFactory
import com.jayway.restassured.path.json.JsonPath
import com.jayway.restassured.response.Response
import com.jayway.restassured.specification.RequestSender
import com.jayway.restassured.specification.RequestSpecification
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
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.pogo.experiments.SubjectAssessor
import org.nrg.xnat.pogo.extensions.SimpleResourceFileExtension
import org.nrg.xnat.pogo.extensions.project.ProjectQueryPutExtension
import org.nrg.xnat.pogo.extensions.session_assessor.SessionAssessorQueryPutExtension
import org.nrg.xnat.pogo.extensions.subject.SubjectQueryPutExtension
import org.nrg.xnat.pogo.extensions.subject_assessor.SubjectAssessorQueryPutExtension
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.pogo.resources.ResourceFile
import org.nrg.xnat.pogo.users.User
import org.nrg.xnat.pogo.users.UserGroup
import org.nrg.xnat.rest.SerializationUtils
import org.nrg.xnat.rest.XnatAliasToken
import org.nrg.xnat.rest.XnatSessionFilter

import static com.jayway.restassured.RestAssured.given
import static com.jayway.restassured.config.ObjectMapperConfig.objectMapperConfig
import static com.jayway.restassured.http.ContentType.JSON

abstract class XnatInterface {

    public static final ObjectMapper XNAT_REST_MAPPER = new XnatRestReadWriteObjectMapper()
    protected XnatSessionFilter sessionFilter
    protected String xnatUrl
    protected User authUser
    protected Optional<Boolean> isAdmin = Optional.absent()

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

        if (given().get(CommonUtils.formatUrl(xnatUrl, '/app/template/Login.vm')).statusCode == 200) {
            final XnatSessionFilter sessionFilter = new XnatSessionFilter(user, xnatUrl, allowInsecureSSL)
            if (given().filter(sessionFilter).get(CommonUtils.formatUrl(xnatUrl, '/data/auth')).statusCode == 200) {
                final Response oldResponse = given().filter(sessionFilter).get(CommonUtils.formatUrl(xnatUrl, '/data/version'))
                if (oldResponse.statusCode == 200) {
                    new XnatInterface_1_6(sessionFilter)
                } else {
                    final Response newResponse = given().filter(sessionFilter).get(CommonUtils.formatUrl(xnatUrl, '/xapi/siteConfig/buildInfo'))
                    if (newResponse.statusCode == 200) {
                        final String version = newResponse.jsonPath().getString("version")
                        if (version.startsWith('1.7.0')) {
                            new XnatInterface_1_7_0(sessionFilter)
                        } else if (version.startsWith('1.7.1')) {
                            new XnatInterface_1_7_1(sessionFilter)
                        } else if (version.startsWith('1.7.2')) {
                            new XnatInterface_1_7_2(sessionFilter)
                        } else {
                            new XnatInterface_1_7_3(sessionFilter)
                        }
                    } else {
                        new XnatInterface_1_7_3(sessionFilter)
                    }
                }
            } else {
                throw new AssertionError("Provided credentials don't appear to be valid.")
            }
        } else {
            throw new AssertionError("There doesn't seem to be an XNAT reachable at that address.")
        }
    }

    static XnatInterface authenticate(String xnatUrl, String username, String password, boolean allowInsecureSSL = false) {
        authenticate(xnatUrl, new User(username).password(password), allowInsecureSSL)
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

    protected void prohibitNonadmin() {
        if (!userIsAdmin()) throw new UnsupportedOperationException('You must be an admin to perform this operation.')
    }

    protected void notSupported() {
        throw new UnsupportedOperationException('REST call not supported in this version of XNAT.')
    }


    boolean queryUserAdmin() {
        'Administrator' in queryBase().get(formatXapiUrl("/users/${authUser.username}/roles")).jsonPath().getList("")
    }

    final boolean userIsAdmin() {
        if (!isAdmin.isPresent()) {
            isAdmin = Optional.of(queryUserAdmin())
        }
        isAdmin
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

    String getAccessionNumber(SubjectAssessor subjectAssessor) {
        if (subjectAssessor.primaryProject == null) throw new IllegalArgumentException("subjectAssessor object must have project specified.")

        queryBase().get(projectExperimentsUrl(subjectAssessor.primaryProject)).then().extract().jsonPath().getString("ResultSet.Result.find {it.label == '${subjectAssessor}' }.ID")
    }

    Subject readSubject(String accessionNumber) {
        final Response response = queryBase().queryParam('format', 'json').get(formatRestUrl('subjects', accessionNumber))
        final Subject subject = response.jsonPath().getObject("items.get(0).children.find { it.field == 'demographics' }.items.get(0).data_fields", Subject.class)
        subject.label(response.jsonPath().getString('items.get(0).data_fields.label'))
    }

    def <T extends Experiment> T readExperiment(String accessionNumber, Class<T> experimentClass) {
        final Response response = queryBase().queryParam('format", "json').get(formatRestUrl('experiments', accessionNumber))
        final T experiment = response.jsonPath().getObject('items.get(0).data_fields', experimentClass)
        experiment.dataType(DataType.lookup(response.jsonPath().getString("items.get(0).meta.'xsi:type'")))
    }

    void waitForAutoRun(ImagingSession session, int maxTimeInSeconds = 60) {
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
    }

    void uploadToSessionZipImporter(File sessionZip, Project project) {
        uploadToSessionZipImporter(sessionZip, project, null, null)
    }

    void uploadToSessionZipImporter(File sessionZip, ImagingSession session) {
        if (session.primaryProject == null) throw new IllegalArgumentException('Session must have project object specified to use this shortcut method')
        uploadToSessionZipImporter(sessionZip, session.primaryProject, session.subject, session)
    }

    String userSessionsRestUrl(User user) {
        formatRestUrl("authUser/${user.username}/sessions")
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
        prohibitNonadmin()
        queryBase().contentType(JSON).body(groups).put(formatXapiUrl("/users/${user.username}/groups")).then().assertThat().statusCode(200)
    }

    void makeUserAdmin(User user) {
        assignUserToRoles(user, 'Administrator')
        addUserToGroups(user, 'ALL_DATA_ADMIN')
        user.admin(true)
    }

    private AnonScript readAnonScript(Response response) {
        new AnonScript().contents(
                response.then().assertThat().statusCode(200).and().extract().jsonPath().getString("ResultSet.Result.get(0).script")
        )
    }

    String siteAnonScriptUrl() {
        formatRestUrl('config/edit/image/dicom/script')
    }

    AnonScript readSiteAnonScript() {
        readAnonScript(queryBase().queryParam('format', 'json').get(siteAnonScriptUrl()))
    }

    void setSiteAnonScriptStatus(boolean status) {
        prohibitNonadmin()
        queryBase().queryParam('activate', status).put(formatRestUrl('/config/edit/image/dicom/status')).then().assertThat().statusCode(200)
    }

    void disableSiteAnonScript() {
        setSiteAnonScriptStatus(false)
    }

    void enableSiteAnonScript() {
        setSiteAnonScriptStatus(true)
    }

    String projectAnonScriptUrl(Project project) {
        formatRestUrl("config/edit/projects/${project.id}/image/dicom/script")
    }

    AnonScript readProjectAnonScript(Project project) {
        readAnonScript(queryBase().queryParam('format', 'json').get(projectAnonScriptUrl(project)))
    }

    void setProjectAnonScript(Project project, AnonScript script) {
        queryBase().body(script.getContents()).put(projectAnonScriptUrl(project)).then().assertThat().statusCode(200)
    }

    void setProjectAnonScriptStatus(Project project, boolean status) {
        queryBase().queryParam('activate', status).put(projectAnonScriptUrl(project)).then().assertThat().statusCode(200)
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
                queryBase().contentType(JSON).body(investigator).post(formatXapiUrl('investigators')).jsonPath().getInt("xnatInvestigatordataId")
        )
    }

    void addListedUsersToProject(Project project) {
        project.users.each { group, userList ->
            userList.each { user ->
                addUserToProject(user, project, group)
            }
        }
    }

    void addUserToProject(User addedUser, Project project, UserGroup userGroup) { // TODO: does this work for Custom User groups?
        addUserToGroups(addedUser, "${project.id}_${userGroup.singularName().toLowerCase()}")
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
            queryBase().queryParams(SerializationUtils.serializeToMap(file)).multiPart(file.extension.getJavaFile()).put(resourceFileUrl(resource, file)).then().assertThat().statusCode(200)
        }
    }

    String resourceFilesUrl(Resource resource) {
        formatXnatUrl("${resource.resourceUrl()}/resources/${resource.folder}/files")
    }

    String resourceFileUrl(Resource resource, ResourceFile file) {
        CommonUtils.formatUrl(resourceFilesUrl(resource), file.name)
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

    void createProject(Project project) {
        if (project == null) {
            throw new UnsupportedOperationException("project cannot be null")
        }

        if (project.extension == null) {
            project.extension(new ProjectQueryPutExtension(this, project))
        }

        project.extension.create()

        if (project.prearchiveCode != null) setPrearchiveSetting(project, project.prearchiveCode)

        createInvestigators((project.pi != null) ? project.investigators + project.pi : project.investigators)

        addListedUsersToProject(project)

        project.projectResources.each { resource ->
            resource.project(project)
        }
        uploadResources(project.projectResources)

        project.subjects.each { subject ->
            createSubject(subject.project(project))
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

        subject.experiments.each { subjectAssessor ->
            createSubjectAssessor(project, subject, subjectAssessor)
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

            session.scans.each { scan ->
                createScan(project, subject, session, scan)
            }

            session.assessors.each { assessor ->
                createSessionAssessor(project, subject, session, assessor)
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
