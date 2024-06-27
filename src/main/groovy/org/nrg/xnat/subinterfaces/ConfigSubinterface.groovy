package org.nrg.xnat.subinterfaces

import io.restassured.path.json.JsonPath
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.nrg.xnat.enums.PetMrProcessingSetting
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.meta.RequireAdmin
import org.nrg.xnat.pogo.AnonScript
import org.nrg.xnat.pogo.ConfigServiceObject
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.SiteConfig
import org.nrg.xnat.pogo.SiteConfigProps
import org.nrg.xnat.pogo.Uptime
import org.nrg.xnat.pogo.dicom.FilterMode
import org.nrg.xnat.pogo.dicom.SeriesImportFilter
import org.nrg.xnat.rest.NotFoundException

import org.nrg.xnat.rest.SerializationUtils

import static io.restassured.http.ContentType.*

class ConfigSubinterface extends CoreXnatFunctionalitySubinterface {

    private static final Matcher CONFIG_PUT_OK = Matchers.oneOf(200, 201)
    private static final String SERIES_IMPORT_FILTER_TOOL = 'seriesImportFilter'
    private static final String SERIES_IMPORT_FILTER_PATH = 'config'

    @Override
    List<String> getHandledEndpoints() {
        [
                '/config/edit/image/dicom/{RESOURCE}',
                '/config/edit/projects/{PROJECT_ID}/image/dicom/{RESOURCE}',
                '/xapi/siteConfig',
                '/xapi/siteConfig/{property}',
                '/xapi/siteConfig/buildInfo',
                '/xapi/siteConfig/uptime'
        ]
    }

    String getBuildInfo() {
        final JsonPath buildPath = queryBase().get(formatXapiUrl('/siteConfig/buildInfo')).then().assertThat().statusCode(200).and().extract().jsonPath()
        "Version ${buildPath.get('version')} (commit ${buildPath.get('commit')})"
    }

    Uptime readUptime() {
        queryBase().get(formatXapiUrl("/siteConfig/uptime")).as(Uptime)
    }

    SiteConfig readSiteConfig() {
        retrieveSiteConfig().as(SiteConfig)
    }

    Map<String, Object> readSiteConfigAsMap() {
        retrieveSiteConfig().as(Map)
    }

    String readSiteConfigPreference(String preference) {
        queryBaseWithStatusCodeListeners([FORBIDDEN_403])
                .get(formatXapiUrl("/siteConfig/${preference}"))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .asString()
    }

    void postToSiteConfig(Map configSettings) {
        postToSiteConfigFrom(configSettings)
    }

    void postToSiteConfig(SiteConfig siteConfig) {
        postToSiteConfigFrom(siteConfig)
    }

    @RequireAdmin
    protected void postToSiteConfigFrom(Object siteConfig) {
        performPostToSiteConfigFrom(siteConfig)
    }

    @RequireAdmin
    void postSiteConfigProperty(String propName, Object propValue) {
        queryBaseWithStatusCodeListeners([FORBIDDEN_403])
                .contentType(TEXT)
                .body(propValue)
                .post(formatXapiUrl("/siteConfig/${propName}"))
                .then()
                .assertThat()
                .statusCode(200)
    }

    void initializeXnat() {
        performPostToSiteConfigFrom(new SiteConfig(initialized: true)) // if we need to initialize, we can't do the admin check
    }

    void setLoginRequirement(boolean loginRequired) {
        postToSiteConfig(new SiteConfig(requireLogin: loginRequired))
    }

    void openXnat() {
        setLoginRequirement(false)
    }

    void closeXnat() {
        setLoginRequirement(true)
    }

    void setSessionXmlRebuilderTimes(int interval, int schedule) {
        postToSiteConfig(new SiteConfig(sessionXmlRebuilderInterval: interval, sessionXmlRebuilderSchedule: schedule))
    }

    void setNonadminProjectSetting(boolean allowed) {
        postToSiteConfig(new SiteConfig(allowNonadminProjectCreation: allowed))
    }

    void setSiteUserListRestriction(boolean restricted) {
        postToSiteConfig(new SiteConfig(restrictUserListToAdmins: restricted))
    }

    void setCrossModalityMergePreventionFlag(boolean status) {
        postToSiteConfig(new SiteConfig(preventCrossModalityMerge: status))
    }

    void setUseSopInstanceUidToUniquelyIdentifyDicom(boolean useUid) {
        postToSiteConfig(new SiteConfig(useSopInstanceUidToUniquelyIdentifyDicom: useUid))
    }

    String readDicomFileNamer() {
        readSiteConfigPreference(SiteConfigProps.DICOM_FILE_NAME_TEMPLATE)
    }

    void updateDicomFileNamer(String newTemplate) {
        postToSiteConfig(new SiteConfig(dicomFileNameTemplate: newTemplate))
    }

    boolean readPostArchiveAnonStatus() {
        Boolean.parseBoolean(readSiteConfigPreference(SiteConfigProps.POST_ARCHIVE_PROJECT_ANON))
    }

    void setPostArchiveAnonStatus(boolean allowed) {
        postToSiteConfig(new SiteConfig(rerunProjectAnonOnRename: allowed))
    }

    void enablePostArchiveAnon() {
        setPostArchiveAnonStatus(true)
    }

    void disablePostArchiveAnon() {
        setPostArchiveAnonStatus(false)
    }

    void setSiteBackupSettings(boolean backupDeletedToCache, boolean maintainFileHistory) {
        postToSiteConfig(new SiteConfig(backupDeletedToCache: backupDeletedToCache, maintainFileHistory: maintainFileHistory))
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
        postToSiteConfig(new SiteConfig(enableSiteAnonScript: status))
    }

    void disableSiteAnonScript() {
        setSiteAnonScriptStatus(false)
    }

    void enableSiteAnonScript() {
        setSiteAnonScriptStatus(true)
    }

    void setSiteAnonScript(AnonScript script) {
        postToSiteConfig(new SiteConfig(siteAnonScript: script.getContents()))
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

    SeriesImportFilter readSiteSeriesImportFilter() {
        final SiteConfig siteConfig = readSiteConfig()
        new SeriesImportFilter(
                enabled: siteConfig.enableSitewideSeriesImportFilter,
                filterMode: FilterMode.lookup(siteConfig.sitewideSeriesImportFilterMode),
                filterBody: siteConfig.sitewideSeriesImportFilter
        )
    }

    void setSiteSeriesImportFilter(SeriesImportFilter seriesImportFilter) {
        postToSiteConfig(
                new SiteConfig(
                        enableSitewideSeriesImportFilter: seriesImportFilter.enabled,
                        sitewideSeriesImportFilterMode: seriesImportFilter.filterMode.jsonRepresentation(),
                        sitewideSeriesImportFilter: seriesImportFilter.filterBody
                )
        )
    }

    SeriesImportFilter readProjectSeriesImportFilter(Project project) {
        final ConfigServiceObject configServiceObject
        try {
            configServiceObject = readConfigToolPath(project, SERIES_IMPORT_FILTER_TOOL, SERIES_IMPORT_FILTER_PATH)
        } catch (NotFoundException ignored) {
            return null
        }
        XnatInterface.XNAT_REST_MAPPER
                .readValue(configServiceObject.contents, SeriesImportFilter)
                .enabled(configServiceObject.status == ConfigServiceObject.Status.ENABLED)
    }

    /*
        Yes, this method is a little weird, and seems as if I'm not getting some reuse out of the config service API wrapping
        in other parts of the class. However, this is much closer to how the front-end interacts with the API here,
        so I'd like to closely match that.
     */
    void setProjectSeriesImportFilter(Project project, SeriesImportFilter seriesImportFilter) {
        final RequestSpecification statusUpdate = queryBase()
                .queryParam('status', seriesImportFilter.enabled ? ConfigServiceObject.Status.ENABLED : ConfigServiceObject.Status.DISABLED)
        final String url = configServiceUrl(project, SERIES_IMPORT_FILTER_TOOL, SERIES_IMPORT_FILTER_PATH)
        final Response response
        if (seriesImportFilter.enabled) {
            response = statusUpdate
                .queryParam('inbody', true)
                .contentType(TEXT) // yeah...
                .body(XnatInterface.XNAT_REST_MAPPER.writeValueAsString(['enabled': true, 'list': seriesImportFilter.filterBody, 'mode': seriesImportFilter.filterMode]))
                .put(url)
        } else {
            response = statusUpdate.put(url)
        }
        response.then().assertThat().statusCode(CONFIG_PUT_OK)
    }

    void setSitePetMrSetting(PetMrProcessingSetting petMrSetting) {
        if (petMrSetting == PetMrProcessingSetting.DEFAULT_TO_SITE) {
            throw new UnsupportedOperationException('Provided PET-MR setting not available at site level')
        }
        postToSiteConfig(new SiteConfig(petMrSetting: petMrSetting.apiValue))
    }

    void setProjectPetMrSetting(Project project, PetMrProcessingSetting petMrSetting) {
        final String url = formatRestUrl('/projects', project.id, '/config/separatePETMR/config')
        if (petMrSetting == PetMrProcessingSetting.DEFAULT_TO_SITE) {
            queryBase().delete(url).then().assertThat().statusCode(200)
        } else {
            queryBase().queryParam('inbody', true).body(petMrSetting.apiValue).put(url).then().assertThat().statusCode(CONFIG_PUT_OK)
        }
    }

    String configServiceUrl(Project project = null, String tool = null, String path = null) {
        final List<String> urlFragments = []
        if (project != null) {
            urlFragments << '/projects/' + project.id
        }
        urlFragments << '/config'
        if (tool != null) {
            urlFragments << tool
        }
        if (path != null) {
            urlFragments << path
        }
        formatRestUrl(urlFragments.toArray([] as String[]))
    }

    List<String> listConfigTools(Project project = null) {
        jsonQuery().get(configServiceUrl(project)).jsonPath().getList("ResultSet.Result.tool")
    }

    List<ConfigServiceObject> readConfigTool(Project project, String tool) {
        SerializationUtils.deserializeList(jsonQuery().get(configServiceUrl(project, tool)).jsonPath().getList('ResultSet.Result'), ConfigServiceObject)
    }

    ConfigServiceObject readConfigToolPath(Project project, String tool, String path, Integer version = null) {
        final JsonPath jsonPath = jsonQueryWithStatusCodeListeners([NOT_FOUND_404])
                .queryParams(makeVersionQueryParams(version))
                .get(configServiceUrl(project, tool, path))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .jsonPath()
                .setRootPath('ResultSet.Result')
        if (jsonPath.getInt('size()') > 1) {
            throw new RuntimeException('Unexpected response from config service.')
        }
        jsonPath.getObject('get(0)', ConfigServiceObject)
    }

    ConfigServiceObject readConfigToolPath(String tool, String path, Integer version = null) {
        readConfigToolPath(null, tool, path, version)
    }

    String readConfigToolPathContents(Project project, String tool, String path, Integer version = null) {
        final Map<String, Object> queryParams = makeVersionQueryParams(version)
        queryParams.put('contents', true)
        queryBase().queryParams(queryParams).get(configServiceUrl(project, tool, path)).then().assertThat().statusCode(200).and().extract().asString()
    }

    String readConfigToolPathContents(String tool, String path, Integer version = null) {
        readConfigToolPathContents(null, tool, path, version)
    }

    List<ConfigServiceObject> readConfigTool(String tool) {
        readConfigTool(null, tool)
    }

    XnatInterface putConfig(Project project, String tool, String path, String content) {
        queryBase().contentType(TEXT).body(content).put(configServiceUrl(project, tool, path)).then().assertThat().statusCode(CONFIG_PUT_OK)
        xnatInterface
    }

    XnatInterface putConfig(String tool, String path, String content) {
        putConfig(null, tool, path, content)
    }

    XnatInterface putConfig(Project project, String tool, String path, ConfigServiceObject configServiceObject) {
        queryBase().contentType(JSON).body(configServiceObject).put(configServiceUrl(project, tool, path)).then().assertThat().statusCode(CONFIG_PUT_OK)
        xnatInterface
    }

    XnatInterface putConfig(String tool, String path, ConfigServiceObject configServiceObject) {
        putConfig(null, tool, path, configServiceObject)
    }

    XnatInterface setConfigStatus(Project project, String tool, String path, ConfigServiceObject.Status status) {
        queryBase().queryParam('status', status).put(configServiceUrl(project, tool, path)).then().assertThat().statusCode(200)
        xnatInterface
    }

    XnatInterface setConfigStatus(String tool, String path, ConfigServiceObject.Status status) {
        setConfigStatus(null, tool, path, status)
    }

    protected Response retrieveSiteConfig() {
        queryBase().contentType(JSON).get(formatXapiUrl('siteConfig'))
    }

    protected void performPostToSiteConfigFrom(Object siteConfig) {
        queryBaseWithStatusCodeListeners([FORBIDDEN_403]).contentType(JSON).body(siteConfig).post(formatXapiUrl('siteConfig')).then().assertThat().statusCode(200)
    }

    protected Map<String, Object> makeVersionQueryParams(Integer version) {
        version == null ? [:] : ['version': version]
    }

}
