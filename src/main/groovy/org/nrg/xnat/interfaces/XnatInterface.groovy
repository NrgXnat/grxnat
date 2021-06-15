package org.nrg.xnat.interfaces

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Optional
import com.jayway.restassured.RestAssured
import com.jayway.restassured.config.JsonConfig
import com.jayway.restassured.config.LogConfig
import com.jayway.restassured.config.RestAssuredConfig
import com.jayway.restassured.mapper.factory.Jackson2ObjectMapperFactory
import com.jayway.restassured.path.json.config.JsonPathConfig
import com.jayway.restassured.response.Response
import com.jayway.restassured.specification.RequestSpecification
import org.hamcrest.Matchers
import org.nrg.testing.CommonStringUtils
import org.nrg.testing.DicomUtils
import org.nrg.testing.FileIOUtils
import org.nrg.xnat.XnatConnectionConfig
import org.nrg.xnat.enums.*
import org.nrg.xnat.jackson.mappers.XnatRestReadWriteObjectMapper
import org.nrg.xnat.jackson.mappers.YamlObjectMapper
import org.nrg.xnat.meta.RequireAdmin
import org.nrg.xnat.pogo.*
import org.nrg.xnat.pogo.custom_variable.CustomVariableContainer
import org.nrg.xnat.pogo.experiments.*
import org.nrg.xnat.pogo.resources.*
import org.nrg.xnat.pogo.users.User
import org.nrg.xnat.rest.SerializationUtils
import org.nrg.xnat.rest.XnatAuthProvider
import org.nrg.xnat.rest.XnatSessionFilter
import org.nrg.xnat.subinterfaces.*
import org.nrg.xnat.versions.XnatVersion
import org.nrg.xnat.versions.XnatVersionList
import org.nrg.xnat.versions.Xnat_1_6dev

import static com.jayway.restassured.RestAssured.given
import static com.jayway.restassured.config.ObjectMapperConfig.objectMapperConfig
import static com.jayway.restassured.http.ContentType.JSON
import static com.jayway.restassured.http.ContentType.URLENC
import static org.nrg.testing.CommonStringUtils.formatUrl

@SuppressWarnings(['GroovyUnusedDeclaration'])
abstract class XnatInterface {

    boolean readResources = true
    User authUser
    public static final ObjectMapper XNAT_REST_MAPPER = new XnatRestReadWriteObjectMapper()
    protected Class<? extends XnatVersion> versionClass
    protected XnatSessionFilter sessionFilter
    protected String xnatUrl
    protected Optional<Boolean> isAdmin = Optional.absent()
    protected List<XnatPlugin> installedPlugins
    protected final List<XnatFunctionalitySubinterface> subinterfaces = []
    @Delegate protected AliasTokenSubinterface aliasTokenSubinterface
    @Delegate protected ConfigSubinterface configSubinterface
    @Delegate protected ContainerServiceSubinterface containerServiceSubinterface
    @Delegate protected DicomSCPSubinterface dicomSCPSubinterface
    @Delegate protected ExperimentSubinterface experimentSubinterface
    @Delegate protected InvestigatorSubinterface investigatorSubinterface
    @Delegate protected PrearchiveAndDirectArchiveSubinterface prearchiveAndDirectArchiveSubinterface
    @Delegate protected ProjectSubinterface projectSubinterface
    @Delegate protected ReconstructionSubinterface reconstructionSubinterface
    @Delegate protected ResourceSubinterface resourceSubinterface
    @Delegate protected ScanSubinterface scanSubinterface
    @Delegate protected SessionAssessorSubinterface sessionAssessorSubinterface
    @Delegate protected SubjectAssessorSubinterface subjectAssessorSubinterface
    @Delegate protected SubjectSubinterface subjectSubinterface
    @Delegate protected UserManagementSubinterface userManagementSubinterface
    @Delegate protected WorkflowSubinterface workflowSubinterface
    @Delegate protected BatchLaunchSubinterface batchLaunchSubinterface
    @Delegate protected EventServiceSubinterface eventServiceSubinterface

    protected XnatInterface() {}

    protected XnatInterface withFilter(XnatSessionFilter sessionFilter) {
        xnatUrl = sessionFilter.xnatUrl
        authUser = sessionFilter.user
        this.sessionFilter = sessionFilter
        this
    }

    protected XnatInterface fromVersion(Class<? extends XnatVersion> versionClass) {
        this.versionClass = versionClass
        subinterfaces.clear()
        aliasTokenSubinterface = constructSubinterface(AliasTokenSubinterface)
        configSubinterface = constructSubinterface(ConfigSubinterface)
        containerServiceSubinterface = constructSubinterface(ContainerServiceSubinterface)
        dicomSCPSubinterface = constructSubinterface(DicomSCPSubinterface)
        experimentSubinterface = constructSubinterface(ExperimentSubinterface)
        investigatorSubinterface = constructSubinterface(InvestigatorSubinterface)
        prearchiveAndDirectArchiveSubinterface = constructSubinterface(PrearchiveAndDirectArchiveSubinterface)
        projectSubinterface = constructSubinterface(ProjectSubinterface)
        reconstructionSubinterface = constructSubinterface(ReconstructionSubinterface)
        resourceSubinterface = constructSubinterface(ResourceSubinterface)
        scanSubinterface = constructSubinterface(ScanSubinterface)
        sessionAssessorSubinterface = constructSubinterface(SessionAssessorSubinterface)
        subjectAssessorSubinterface = constructSubinterface(SubjectAssessorSubinterface)
        subjectSubinterface = constructSubinterface(SubjectSubinterface)
        userManagementSubinterface = constructSubinterface(UserManagementSubinterface)
        workflowSubinterface = constructSubinterface(WorkflowSubinterface)
        batchLaunchSubinterface = constructSubinterface(BatchLaunchSubinterface)
        eventServiceSubinterface = constructSubinterface(EventServiceSubinterface)
        this
    }

    static XnatInterface authenticate(String xnatUrl, XnatAuthProvider userAuth, XnatConnectionConfig connectionConfig = new XnatConnectionConfig()) {
        RestAssured.config = RestAssuredConfig.config().
                jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE)).
                logConfig(connectionConfig.logOnValidationFailure ? LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails() : LogConfig.logConfig()).
                objectMapperConfig(objectMapperConfig().jackson2ObjectMapperFactory(
                        new Jackson2ObjectMapperFactory() {
                            @Override
                            ObjectMapper create(Class aClass, String s) {
                                XNAT_REST_MAPPER
                            }
                        }
        ))
        if (connectionConfig.allowInsecureSSL) {
            RestAssured.useRelaxedHTTPSValidation() // TODO: make this not global
        }

        switch (given().get(formatUrl(xnatUrl, '/app/template/Login.vm')).statusCode) {
            case 200:
                return performLogin(xnatUrl, userAuth, connectionConfig)
            case 302:
                throw new RuntimeException('Attempting to check availability of XNAT login page returned a 302 status code. Is the provided protocol (http versus https) correct?')
            default:
                throw new RuntimeException("There doesn't seem to be an XNAT reachable at that address.")
        }
    }

    static XnatInterface authenticate(String xnatUrl, String username, String password, XnatConnectionConfig connectionConfig = new XnatConnectionConfig()) {
        authenticate(xnatUrl, new User(username).password(password), connectionConfig)
    }

    protected static XnatInterface performLogin(String xnatUrl, XnatAuthProvider userAuth, XnatConnectionConfig connectionConfig) {
        final XnatSessionFilter sessionFilter = userAuth.createSessionFilter(xnatUrl, connectionConfig.allowInsecureSSL)
        if (!connectionConfig.skipAuth && given().filter(sessionFilter).get(formatUrl(xnatUrl, '/data/auth')).statusCode != 200) {
            throw new RuntimeException("Provided credentials don't appear to be valid.")
        }
        final Class<? extends XnatVersion> versionClass = connectionConfig.versionClass ?: determineVersionClass(xnatUrl, sessionFilter)
        versionClass.newInstance().interfaceClass.newInstance().withFilter(sessionFilter).fromVersion(versionClass)
    }

    protected static Class<? extends XnatVersion> determineVersionClass(String xnatUrl, XnatSessionFilter sessionFilter) {
        final Response oldResponse = given().filter(sessionFilter).get(formatUrl(xnatUrl, '/data/version'))
        if (oldResponse.statusCode == 200 && !oldResponse.asString().contains('<!')) {
            Xnat_1_6dev
        } else {
            final Response newResponse = given().filter(sessionFilter).get(formatUrl(xnatUrl, '/xapi/siteConfig/buildInfo'))
            if (newResponse.statusCode == 200 && newResponse.getContentType().contains('json')) {
                final String version = newResponse.jsonPath().getString('version')
                XnatVersionList.lookup(version)
            } else {
                XnatVersionList.lookup('unknown')
            }
        }
    }

    protected <X extends XnatFunctionalitySubinterface> X constructSubinterface(Class<X> subinterfaceClass) {
        final X subinterface = XnatSubinterfaceVersionManager.lookupSubinterface(versionClass, subinterfaceClass).newInstance()
        subinterface.setXnatInterface(this)
        subinterfaces << subinterface
        subinterface
    }

    /**
     * Searches for the {@link XnatFunctionalitySubinterface} subclass which implements the provided REST endpoint
     * @param restEndpoint the XNAT REST path, exactly as written in xnat-web's XNATApplication.java (or XnatRestlet-annotated class) for restlet APIs, and exactly as listed in Swagger for XAPIs with /xapi prefixed (e.g. "/xapi/siteConfig")
     * @return The implementing subinterface, or null if none available
     */
    Class<? extends XnatFunctionalitySubinterface> lookupSubinterface(String restEndpoint) {
        subinterfaces.find { subinterface ->
            subinterface.handledEndpoints.contains(restEndpoint)
        }?.class
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

    @Deprecated
    protected void notSupported() {
        throw new UnsupportedOperationException('REST call not supported in this version of XNAT.')
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

    String jsessionId() {
        sessionFilter.sessionId
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
        if (session != null) {
            getAccessionNumber(project, session)
        }
    }

    void uploadToSessionZipImporter(File sessionZip, Project project) {
        uploadToSessionZipImporter(sessionZip, project, null, null)
    }

    void uploadToSessionZipImporter(File sessionZip, ImagingSession session) {
        if (session.primaryProject == null) throw new IllegalArgumentException('Session must have project object specified to use this shortcut method')
        uploadToSessionZipImporter(sessionZip, session.primaryProject, session.subject, session)
    }

    List<XnatPlugin> readInstalledPlugins() {
        if (installedPlugins == null) {
            installedPlugins = SerializationUtils.deserializeList(queryBase().get(formatXapiUrl('plugins')).then().assertThat().statusCode(200).and().extract().jsonPath().getList('values().toList()'), XnatPlugin)
        }
        installedPlugins
    }

    @RequireAdmin
    void setDicomRoutingConfig(RoutingRulesType routingRulesType, String contents) {
        final String url = formatRestUrl('config', 'dicom', routingRulesType.configPath)
        final Map<String, String> params = [status : 'enabled', 'contents' : contents]
        queryBase().contentType(JSON).body(params).put(url).then().assertThat().statusCode(Matchers.isOneOf(200, 201))
    }

    void setProjectDicomRoutingConfig(String contents) {
        setDicomRoutingConfig(RoutingRulesType.PROJECT_RULES, contents)
    }

    void setDicomProjectRulesFrom(int dicomElement, String regex) {
        setProjectDicomRoutingConfig("${DicomUtils.intToFullHexString(dicomElement)}:${regex}")
    }

    void setSubjectDicomRoutingConfig(String contents) {
        setDicomRoutingConfig(RoutingRulesType.SUBJECT_RULES, contents)
    }

    void setSessionDicomRoutingConfig(String contents) {
        setDicomRoutingConfig(RoutingRulesType.SESSION_RULES, contents)
    }

    @RequireAdmin
    void disableDicomRoutingConfig(RoutingRulesType routingRulesType) {
        final String url = formatRestUrl('config', 'dicom', routingRulesType.configPath)
        queryBase().queryParam('status', 'disabled').put(url).then().assertThat().statusCode(200)
    }

    void disableProjectDicomRoutingConfig() {
        disableDicomRoutingConfig(RoutingRulesType.PROJECT_RULES)
    }

    void disableSubjectDicomRoutingConfig() {
        disableDicomRoutingConfig(RoutingRulesType.SUBJECT_RULES)
    }

    void disableSessionDicomRoutingConfig() {
        disableDicomRoutingConfig(RoutingRulesType.SESSION_RULES)
    }

    @RequireAdmin
    void setupDataType(DataType dataType) {
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

    void setupProjectEventSubscriptions(Project project) {
        project.subscriptions.each { subscription ->
            subscription.eventFilter.setProjectIds([project.id])
            queryBase().body(subscription).contentType(JSON).post(formatXapiUrl('/events/subscription')).then().assertThat().statusCode(201)
        }
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

}
