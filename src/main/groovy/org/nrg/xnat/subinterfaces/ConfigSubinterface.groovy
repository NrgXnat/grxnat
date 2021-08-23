package org.nrg.xnat.subinterfaces

import com.jayway.restassured.http.ContentType
import com.jayway.restassured.path.json.JsonPath
import com.jayway.restassured.response.Response
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.nrg.xnat.enums.PetMrProcessingSetting
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.meta.RequireAdmin
import org.nrg.xnat.pogo.AnonScript
import org.nrg.xnat.pogo.ConfigServiceObject
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.SiteConfig
import org.nrg.xnat.rest.SerializationUtils

import static com.jayway.restassured.http.ContentType.*

class ConfigSubinterface extends XnatFunctionalitySubinterface {

    private static final Matcher CONFIG_PUT_OK = Matchers.isOneOf(200, 201)

    @Override
    List<String> getHandledEndpoints() {
        [
                '/config/edit/image/dicom/{RESOURCE}',
                '/config/edit/projects/{PROJECT_ID}/image/dicom/{RESOURCE}',
                '/xapi/siteConfig',
                '/xapi/siteConfig/buildInfo'
        ]
    }

    String getBuildInfo() {
        final JsonPath buildPath = queryBase().get(formatXapiUrl('/siteConfig/buildInfo')).then().assertThat().statusCode(200).and().extract().jsonPath()
        "Version ${buildPath.get('version')} (commit ${buildPath.get('commit')})"
    }

    @RequireAdmin
    SiteConfig readSiteConfig() {
        queryBase().contentType(JSON).get(formatXapiUrl('siteConfig')).as(SiteConfig)
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
            queryBase().queryParam('inbody', true).body(petMrSetting.apiValue).put(url).then().assertThat().statusCode(201)
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
        final JsonPath jsonPath = jsonQuery().queryParams(version == null ? [:] : ['version': version]).get(configServiceUrl(project, tool, path)).
                jsonPath().setRoot('ResultSet.Result')
        if (jsonPath.getInt('size()') > 1) {
            throw new RuntimeException('Unexpected response from config service.')
        }
        jsonPath.getObject('get(0)', ConfigServiceObject)
    }

    ConfigServiceObject readConfigToolPath(String tool, String path, Integer version = null) {
        readConfigToolPath(null, tool, path, version)
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

    protected void performPostToSiteConfigFrom(Object siteConfig) {
        queryBase().contentType(JSON).body(siteConfig).post(formatXapiUrl('siteConfig')).then().assertThat().statusCode(200)
    }

}
