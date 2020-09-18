package org.nrg.xnat.interfaces

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Optional
import com.jayway.restassured.RestAssured
import com.jayway.restassured.config.JsonConfig
import com.jayway.restassured.config.RestAssuredConfig
import com.jayway.restassured.http.ContentType
import com.jayway.restassured.mapper.factory.Jackson2ObjectMapperFactory
import com.jayway.restassured.path.json.JsonPath
import com.jayway.restassured.path.json.config.JsonPathConfig
import com.jayway.restassured.response.Response
import com.jayway.restassured.specification.RequestSpecification
import groovyx.gpars.GParsPool
import org.apache.commons.lang3.time.StopWatch
import org.hamcrest.Matchers
import org.nrg.testing.CommonStringUtils
import org.nrg.testing.DicomUtils
import org.nrg.testing.FileIOUtils
import org.nrg.testing.TimeUtils
import org.nrg.xnat.enums.Accessibility
import org.nrg.xnat.enums.PrearchiveCode
import org.nrg.xnat.enums.RoutingRulesType
import org.nrg.xnat.enums.SiteDataRole
import org.nrg.xnat.jackson.mappers.XnatRestReadWriteObjectMapper
import org.nrg.xnat.jackson.mappers.YamlObjectMapper
import org.nrg.xnat.pogo.AnonScript
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Investigator
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Reconstruction
import org.nrg.xnat.pogo.Share
import org.nrg.xnat.pogo.SiteConfig
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.XnatPlugin
import org.nrg.xnat.pogo.custom_variable.CustomVariableContainer
import org.nrg.xnat.pogo.experiments.Experiment
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.NonimagingAssessor
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.pogo.experiments.SubjectAssessor
import org.nrg.xnat.pogo.extensions.SimpleResourceFileExtension
import org.nrg.xnat.pogo.extensions.project.ProjectQueryPutExtension
import org.nrg.xnat.pogo.extensions.reconstruction.ReconstructionQueryPutExtension
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

import java.util.spi.ResourceBundleControlProvider

import static com.jayway.restassured.RestAssured.given
import static com.jayway.restassured.config.ObjectMapperConfig.objectMapperConfig
import static com.jayway.restassured.http.ContentType.JSON
import static com.jayway.restassured.http.ContentType.URLENC
import static org.nrg.xnat.enums.DataAccessLevel.*
import static org.nrg.testing.CommonStringUtils.formatUrl

@SuppressWarnings(['GroovyUnusedDeclaration', 'GrMethodMayBeStatic'])
abstract class XnatInterface {

    private static final ADMIN_ROLE = 'Administrator'
    public static final ObjectMapper XNAT_REST_MAPPER = new XnatRestReadWriteObjectMapper()
    protected XnatSessionFilter sessionFilter
    protected String xnatUrl
    protected User authUser
    protected Optional<Boolean> isAdmin = Optional.absent()
    protected boolean readResources = true

    protected XnatInterface() {}

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
        )).jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE))
        if (allowInsecureSSL) RestAssured.useRelaxedHTTPSValidation()

        if (given().get(formatUrl(xnatUrl, '/app/template/Login.vm')).statusCode == 200) {
            final XnatSessionFilter sessionFilter = new XnatSessionFilter(user, xnatUrl, allowInsecureSSL)
            if (given().filter(sessionFilter).get(formatUrl(xnatUrl, '/data/auth')).statusCode == 200) {
                final Response oldResponse = given().filter(sessionFilter).get(formatUrl(xnatUrl, '/data/version'))
                if (oldResponse.statusCode == 200 && !oldResponse.asString().contains('<!')) {
                    new XnatInterface_1_6(sessionFilter)
                } else {
                    final Response newResponse = given().filter(sessionFilter).get(formatUrl(xnatUrl, '/xapi/siteConfig/buildInfo'))
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
                throw new RuntimeException("Provided credentials don't appear to be valid.")
            }
        } else {
            throw new RuntimeException("There doesn't seem to be an XNAT reachable at that address.")
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

    void removeCachedAuth() {
        sessionFilter.deleteSessionId()
    }

    void reauthenticate() {
        sessionFilter.extractSessionId()
    }

    void regenerateUserSession() {
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
        formatUrl(xnatUrl, formatUrl((Object[]) components))
    }

    String formatRestUrl(String... components) {
        formatXnatUrl('data', formatUrl((Object[]) components))
    }

    String formatXapiUrl(String... components) {
        formatXnatUrl('xapi', formatUrl((Object[]) components))
    }

    String readXnatCsrfToken() {
        queryBase().queryParam('CSRF', true).get(formatRestUrl('auth')).then().assertThat().statusCode(200).and().extract().response().asString().split('=')[1]
    }

    RequestSpecification queryBase() {
        given().filter(sessionFilter)
    }

    RequestSpecification jsonQuery() {
        queryBase().queryParam('format', 'json')
    }

    RequestSpecification xmlQuery() {
        queryBase().queryParam('format', 'xml')
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
        response.statusCode == 200 && ADMIN_ROLE in response.jsonPath().getList('')
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
        queryBase().get(issueAliasTokenUrl()).then().assertThat().statusCode(200).and().extract().jsonPath().getObject('', XnatAliasToken) // use longer explicit jsonPath() extraction because content-type from XNAT is text/plain...
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

        final StopWatch stopWatch = TimeUtils.launchStopWatch()
        while (true) {
            TimeUtils.checkStopWatch(stopWatch, maxTimeInSeconds, "AutoRun did not complete in allotted number of seconds: ${maxTimeInSeconds}")

            final String status = queryBase().queryParam("experiment", accessionNumber).queryParam("format", "json").
                    get(formatRestUrl('services/workflows/AutoRun')).then().extract().jsonPath().getString('items.get(0).data_fields.status')

            if (status == 'Complete') {
                return
            } else if (status == 'Failed') {
                throw new AssertionError('AutoRun failed.')
            }
            TimeUtils.sleep(1000)
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
        queryPararms.put('project', project.getId()) // ecat as a zip won't recognize PROJECT_ID

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
            final int status = queryBase().delete(userSessionsRestUrl(targetUser)).statusCode
            switch (status) {
                case 200:
                    break
                case 403:
                    println 'Attempt to expire active sessions returned a 403. This can occur if the authenticating JSESSION has timed out, so a new one will be generated, and the query repeated.'
                    regenerateUserSession()
                    queryBase().delete(userSessionsRestUrl(targetUser)).then().assertThat().statusCode(200)
                    break
                default:
                    throw new AssertionError("Call to expire active sessions returned unexpected status code: ${status}.")
            }
        } else {
            prohibitNonadmin()
        }
    }

    void expireAllActiveSessions() {
        expireAllActiveSessions(authUser)
    }

    @SuppressWarnings("GroovyMissingReturnStatement")
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

    User readUser(String username) {
        queryBase().get(formatXapiUrl("users/profile/${username}")).then().assertThat().statusCode(200).and().extract().response().as(User)
    }

    void createUser(User user) {
        prohibitNonadmin()
        queryBase().contentType(JSON).body(user).post(formatXapiUrl('users')).then().assertThat().statusCode(201)
        if (user.isAdmin()) {
            assignUserToRoles(user, ADMIN_ROLE)
        }
        if (user.dataRole != SiteDataRole.NONE) {
            addUserToGroups(user, user.dataRole.name())
        }
    }

    void updateUser(User user) {
        prohibitNonadmin()
        queryBase().contentType(JSON).body(user).put(formatXapiUrl("users/${user.username}")).then().assertThat().statusCode(200)
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

    void removeUserFromGroups(User user, String... groups) {
        queryBase().contentType(JSON).body(groups).delete(formatXapiUrl("/users/${user.username}/groups")).then().assertThat().statusCode(200)
    }

    void makeUserAdmin(User user) {
        assignUserToRoles(user, ADMIN_ROLE)
        addUserToGroups(user, 'ALL_DATA_ADMIN')
        user.admin(true)
    }

    void postToSiteConfig(Map configSettings) {
        prohibitNonadmin()
        queryBase().contentType(JSON).body(configSettings).post(formatXapiUrl('siteConfig')).then().assertThat().statusCode(200)
    }

    void setLoginRequirement(boolean loginRequired) {
        postToSiteConfig([(SiteConfig.LOGIN_REQUIRED) : loginRequired])
    }

    void openXnat() {
        setLoginRequirement(false)
    }

    void closeXnat() {
        setLoginRequirement(true)
    }

    void setSessionXmlRebuilderTimes(int interval, int schedule) {
        postToSiteConfig([(SiteConfig.AUTOARCHIVE_IDLE_TIME) : interval, (SiteConfig.AUTOARCHIVE_IDLE_SCHEDULE) : schedule])
    }

    void setNonadminProjectSetting(boolean allowed) {
        postToSiteConfig([(SiteConfig.ALLOW_NON_ADMIN_PROJECT_CREATION) : allowed])
    }

    void setSiteUserListRestriction(boolean restricted) {
        postToSiteConfig([(SiteConfig.RESTRICT_USER_LIST_TO_ADMIN) : restricted])
    }

    List<XnatPlugin> readInstalledPlugins() {
        SerializationUtils.deserializeList(queryBase().get(formatXapiUrl('plugins')).then().assertThat().statusCode(200).and().extract().jsonPath().getList('values().toList()'), XnatPlugin)
    }

    void setDicomRoutingConfig(RoutingRulesType routingRulesType, String contents) {
        final String url = formatRestUrl('config', 'dicom', routingRulesType.configPath)
        final Map<String, String> params = [status : 'enabled', 'contents' : contents]
        queryBase().contentType(JSON).body(params).put(url).then().assertThat().statusCode(Matchers.isOneOf(200, 201))
    }

    void disableDicomRoutingConfig(RoutingRulesType routingRulesType) {
        final String url = formatRestUrl('config', 'dicom', routingRulesType.configPath)
        queryBase().queryParam('status', 'disabled').put(url).then().assertThat().statusCode(200)
    }

    void setDicomProjectRules(String ruleString) {
        prohibitNonadmin()
        queryBase().contentType(ContentType.TEXT).body(ruleString).put(formatRestUrl('/config/dicom/projectRules')).then().assertThat().statusCode(Matchers.isOneOf(200, 201))
    }

    void setDicomProjectRulesFrom(int dicomElement, String regex) {
        setDicomProjectRules("${DicomUtils.intToFullHexString(dicomElement)}:${regex}")
    }

    private AnonScript readAnonScript(Response response) {
        new AnonScript().contents(
                response.then().assertThat().statusCode(200).and().extract().jsonPath().getString('ResultSet.Result.get(0).script')
        )
    }

    String legacySiteAnonScriptUrl() {
        formatRestUrl('config/edit/image/dicom/script')
    }

    AnonScript readSiteAnonScript() {
        readAnonScript(jsonQuery().get(legacySiteAnonScriptUrl()))
    }

    void setSiteAnonScriptStatus(boolean status) {
        postToSiteConfig([(SiteConfig.ENABLE_SITEWIDE_ANONYMIZATION_SCRIPT) : status])
    }

    void disableSiteAnonScript() {
        setSiteAnonScriptStatus(false)
    }

    void enableSiteAnonScript() {
        setSiteAnonScriptStatus(true)
    }

    void setSiteAnonScript(AnonScript script) {
        postToSiteConfig([(SiteConfig.SITEWIDE_ANONYMIZATION_SCRIPT) : script.getContents()])
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

    void setupDataType(DataType dataType) {
        prohibitNonadmin()
        final String serializedDataType = CommonStringUtils.replaceEach(FileIOUtils.loadResource('generic_data_type.yaml').text, [
                '$xsiType' : dataType.xsiType,
                '$code' : dataType.code ?: '',
                '$singularName' : dataType.singularName,
                '$pluralName' : dataType.pluralName
        ])

        requestWithCsrfToken().contentType(URLENC).formParams(new YamlObjectMapper().readValue(serializedDataType, Map)).post(formatXnatUrl('/app/action/ElementSecurityWizard')).
                then().assertThat().statusCode(200)
        println("Successfully set up data type: ${dataType.xsiType}...")
    }

    void addListedUsersToProject(Project project) {
        project.users.each { group, userList ->
            userList.each { user ->
                addUserToProject(user, project, group)
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
                ("xnat:projectData_xnat:projectData/ID_${project.id}_R".toString()) : 1,
                (customUserGroupPermissionString(project, DataType.SUBJECT, 'R')): 1
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

    String customUserGroupPermissionString(Project project, DataType dataType, String permission) {
        "${dataType.xsiType}_${dataType.xsiType}/project_${project}_${permission}"
    }

    void setupProjectEventSubscriptions(Project project) {
        project.subscriptions.each { subscription ->
            subscription.eventFilter.setProjectIds([project.id])
            queryBase().body(subscription).contentType(JSON).post(formatXapiUrl('/events/subscription')).then().assertThat().statusCode(201)
        }
    }

    void uploadResources(List<Resource> resources) {
        resources.each { resource ->
            uploadResource(resource)
        }
    }

    void uploadResource(Resource resource) {
        if (resource.folder == null) {
            throw new UnsupportedOperationException('Resource is missing a label (folder).')
        }

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
        formatUrl(resourceFilesUrl(resource), file.name)
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
        resource.resourceFiles(
                jsonQuery().get(
                        formatXnatUrl("${resource.resourceUrl()}/resources/${resource.folder}/files")
                ).jsonPath().getObject('ResultSet.Result', ResourceFile[]) as List<ResourceFile>
        ).resourceFiles
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

    Project readProject(String projectID) {
        final JsonPath projectCall = jsonQuery().get(projectUrl(new Project(projectID))).then().
                assertThat().statusCode(200).and().extract().jsonPath().setRoot('items')

        final Project project = projectCall.getObject('data_fields.get(0)', Project).id(projectID)

        if (projectCall.get("children.get(0).find { it.field == 'aliases/alias' }") != null) {
            project.setAliases(
                    projectCall.getList("children.get(0).find { it.field == 'aliases/alias' }.items.data_fields.alias")
            )
        }

        if (projectCall.get("children.get(0).find { it.field == 'investigators/investigator' }") != null) {
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
        project.secondarySubjects(readSecondarySubjects(project))
        // TODO: anon scripts
    }

    List<Subject> readSubjects(Project project) {
        final List<Subject> subjects = subjectQuery(project, true)

        subjects.each { subject ->
            if (readResources) {
                subject.resources(readResources(new SubjectResource().project(project).subject(subject)))
            }
            subject.experiments(
                readSubjectAssessors(project, subject)
            )
        }
        subjects
        // TODO: shares
    }

    List<Subject> readSecondarySubjects(Project project) {
        subjectQuery(project, false)
        // TODO: fully populate secondary subject objects. Issue: how to handle setting project objects for other projects (i.e. when the project is not the variable "project"). Could make empty project objects, but then attempting to access them later gives incomplete objects.
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    private List<Subject> subjectQuery(Project project, boolean primary) {
        jsonQuery().queryParam('columns', 'label,project,gender,handedness,education,race,ethnicity,group,yob,dob,age,height,weight,src').
                get(projectSubjectsUrl(project)).jsonPath().getObject("ResultSet.Result.findAll { it.project ${primary ? '=' : '!'}= '${project.id}' }", Subject[])
    }

    List<SubjectAssessor> readSubjectAssessors(Project project, Subject subject) {
        List imagingMaps, nonimagingMaps

        (imagingMaps, nonimagingMaps) = jsonQuery().queryParam("columns", "note,date,label,ID").
                get(formatRestUrl("/projects/${project}/subjects/${subject}/experiments")).then().assertThat().statusCode(200).
                and().extract().response().jsonPath().getList('ResultSet.Result').split { it.xsiType.matches('xnat:.+SessionData') }

        final List<NonimagingAssessor> nonimagingSubjectAssessors = SerializationUtils.deserializeList(nonimagingMaps, NonimagingAssessor)
        final List<ImagingSession> imagingSessions = SerializationUtils.deserializeList(imagingMaps, ImagingSession)

        //noinspection GroovyAssignabilityCheck
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
        subject.experiments
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

        setupProjectEventSubscriptions(project)

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

    void createSubject(Project project, Subject subject, boolean suppressAssessors = false) {
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

        putCustomVariableValues(subjectUrl(project, subject), subject, subject.fields)

        if (suppressAssessors) return

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
        formatUrl(projectUrl(project), 'subjects')
    }

    String subjectUrl(Project project, Subject subject) {
        if (subject.label == null) {
            throw new UnsupportedOperationException('subject.label cannot be null')
        }
        formatUrl(projectUrl(project), 'subjects', subject.label)
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
            subjectAssessor.extension(new SubjectAssessorQueryPutExtension(this, subjectAssessor))
        }
        subjectAssessor.extension.create(project, subject)

        subjectAssessor.resources.each { resource ->
            resource.project(project).subject(subject).subjectAssessor(subjectAssessor)
        }
        uploadResources(subjectAssessor.resources)

        subjectAssessor.shares.each { share ->
            shareSubjectAssessor(subjectAssessor, share)
        }

        putCustomVariableValues(subjectAssessorUrl(project, subject, subjectAssessor), subjectAssessor, subjectAssessor.fields)

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

            session.reconstructions.each { reconstruction ->
                createReconstruction(project, subject, session, reconstruction)
            }

            if (suppressAssessors) return

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

    void shareSubjectAssessor(SubjectAssessor subjectAssessor, Share share) {
        if (share.destinationProject == null) {
            throw new UnsupportedOperationException('Destination project string cannot be null for experiment sharing.')
        }

        queryBase().queryParam('label', share.destinationLabel ?: subjectAssessor.label).put(formatUrl(simplifiedSubjectAssessorUrl(subjectAssessor), 'projects', share.destinationProject)).then().assertThat().statusCode(Matchers.isOneOf(200, 201))
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
        queryBase().queryParam('removeFiles', true).delete(subjectAssessorUrl(project, subject, subjectAssessor)).then().assertThat().statusCode(200)
    }

    void deleteSubjectAssessor(SubjectAssessor subjectAssessor) {
        deleteSubjectAssessor(subjectAssessor.getPrimaryProject(), subjectAssessor.getSubject(), subjectAssessor)
    }

    String subjectAssessorUrl(Project project, Subject subject, SubjectAssessor assessor) {
        if (assessor.label == null) {
            throw new UnsupportedOperationException('assessor.label cannot be null')
        }
        formatUrl(subjectUrl(project, subject), 'experiments', assessor.label)
    }

    String subjectAssessorUrl(SubjectAssessor assessor) {
        subjectAssessorUrl(assessor.primaryProject ?: assessor.subject.project, assessor.subject, assessor)
    }

    String simplifiedSubjectAssessorUrl(SubjectAssessor subjectAssessor) {
        formatRestUrl("/experiments/${getAccessionNumber(subjectAssessor)}")
    }

    String sessionScansUrl(Project project, Subject subject, ImagingSession session) {
        formatUrl(subjectAssessorUrl(project, subject, session), 'scans')
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
        if (scan.id == null) {
            throw new UnsupportedOperationException('scan.id cannot be null')
        }
        formatUrl(sessionScansUrl(project, subject, session), scan.id)
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
        formatUrl(subjectAssessorUrl(project, subject, session), 'assessors')
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

        assessor.shares.each { share ->
            shareSessionAssessor(session, assessor, share)
        }

        assessor.resources.each { resource ->
            resource.project(project).subject(subject).subjectAssessor(session).sessionAssessor(assessor)
        }
        uploadResources(assessor.resources)

        putCustomVariableValues(sessionAssessorUrl(project, subject, session, assessor), assessor, assessor.fields)
    }

    void createSessionAssessor(SessionAssessor assessor) {
        createSessionAssessor(assessor.getPrimaryProject(), assessor.getSubject(), assessor.getParentSession(), assessor)
    }

    void shareSessionAssessor(SubjectAssessor session, SessionAssessor sessionAssessor, Share share) {
        if (share.destinationProject == null) {
            throw new UnsupportedOperationException('Destination project string cannot be null for experiment sharing.')
        }

        queryBase().queryParam('label', share.destinationLabel ?: sessionAssessor.label).put(formatUrl(simplifiedSessionAssessorUrl(session, sessionAssessor), 'projects', share.destinationProject)).then().assertThat().statusCode(Matchers.isOneOf(200, 201))
    }

    void deleteSessionAssessor(Project project, Subject subject, ImagingSession session, SessionAssessor sessionAssessor) {
        queryBase().delete(sessionAssessorUrl(project, subject, session, sessionAssessor)).then().assertThat().statusCode(200)
    }

    void deleteSessionAssessor(SessionAssessor sessionAssessor) {
        deleteSessionAssessor(sessionAssessor.getPrimaryProject(), sessionAssessor.getSubject(), sessionAssessor.getParentSession(), sessionAssessor)
    }

    String sessionAssessorUrl(Project project, Subject subject, ImagingSession session, SessionAssessor sessionAssessor) {
        if (sessionAssessor.label == null) {
            throw new UnsupportedOperationException('sessionAssessor.label cannot be null')
        }
        formatUrl(subjectAssessorUrl(project, subject, session), 'assessors', sessionAssessor.label)
    }

    String sessionAssessorUrl(SessionAssessor assessor) {
        sessionAssessorUrl(assessor.getPrimaryProject(), assessor.getSubject(), assessor.getParentSession(), assessor)
    }

    String simplifiedSessionAssessorUrl(SubjectAssessor session, SessionAssessor sessionAssessor) {
        formatUrl(simplifiedSubjectAssessorUrl(session), '/assessors/', sessionAssessor.accessionNumber) // TODO: retrieve ID if needed
    }

    String reconstructionUrl(Project project, Subject subject, ImagingSession session, Reconstruction reconstruction) {
        if (reconstruction.label == null) {
            throw new UnsupportedOperationException('reconstruction.label cannot be null')
        }
        formatUrl(subjectAssessorUrl(project, subject, session), 'reconstructions', reconstruction.label)
    }

    String reconstructionUrl(Reconstruction reconstruction) {
        formatUrl(subjectAssessorUrl(reconstruction.parentSession), 'reconstructions', reconstruction.label)
    }

    void createReconstruction(Project project, Subject subject, ImagingSession session, Reconstruction reconstruction) {
        if (project == null) {
            throw new UnsupportedOperationException('project cannot be null')
        }
        if (subject == null) {
            throw new UnsupportedOperationException('subject cannot be null')
        }
        if (session == null) {
            throw new UnsupportedOperationException('session cannot be null')
        }
        if (reconstruction == null) {
            throw new UnsupportedOperationException('reconstruction cannot be null')
        }

        if (reconstruction.extension == null) {
            reconstruction.extension(new ReconstructionQueryPutExtension(this, reconstruction))
        }

        reconstruction.extension.create(project, subject, session)

        reconstruction.resources.each { resource ->
            resource.project(project).subject(subject).subjectAssessor(session).reconstruction(reconstruction)
        }
        uploadResources(reconstruction.resources)
    }

    void createReconstruction(Reconstruction reconstruction) {
        final ImagingSession session = reconstruction.parentSession
        createReconstruction(session.primaryProject, session.subject, session, reconstruction)
    }

    void deleteProject(Project project) {
        queryBase().queryParam('removeFiles', true).delete(projectUrl(project)).then().assertThat().statusCode(200)
    }

    void putCustomVariableValue(String url, CustomVariableContainer baseObject, String variable, Object value) {
        putCustomVariableValues(url, baseObject, [(variable) : value])
    }

    void putCustomVariableValues(String url, CustomVariableContainer baseObject, Map<String, Object> values) {
        if (values == null || values.isEmpty()) return
        final Map<String, Object> formValues = values.collectEntries { variable, value ->
            ["${baseObject.fieldBaseDataType()}/fields/field[name=${variable.toLowerCase()}]/field", value]
        } as Map<String, Object>
        queryBase().formParams(formValues).put(url).then().assertThat().statusCode(200)
    }

    void deleteAllProjectData(Project project) {
        jsonQuery().queryParam('project', project.id).get(formatRestUrl("/experiments")).jsonPath().getList('ResultSet.Result').reverse().each { experiment -> // reverse list to delete assessors before their parent session
            queryBase().queryParam('removeFiles', true).delete(formatRestUrl("projects/${project.id}/experiments/${experiment.ID}")).then().assertThat().statusCode(200)
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
