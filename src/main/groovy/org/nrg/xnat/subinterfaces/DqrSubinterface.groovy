package org.nrg.xnat.subinterfaces

import io.restassured.common.mapper.TypeRef
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.meta.RequireAdmin
import org.nrg.xnat.meta.RequirePlugin
import org.nrg.xnat.pogo.PluginRegistry
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.dqr.DqrCFindRow
import org.nrg.xnat.pogo.dqr.DqrCMoveSpec
import org.nrg.xnat.pogo.dqr.DqrPatientSearchResponse
import org.nrg.xnat.pogo.dqr.DqrProjectSettings
import org.nrg.xnat.pogo.dqr.DqrQueueHistoryParams
import org.nrg.xnat.pogo.dqr.DqrSeriesSearchResponse
import org.nrg.xnat.pogo.dqr.DqrSettings
import org.nrg.xnat.pogo.dqr.DqrStudyRepresentation
import org.nrg.xnat.pogo.dqr.ExecutedPacsRequest
import org.nrg.xnat.pogo.dqr.PacsAvailability
import org.nrg.xnat.pogo.dqr.PacsConnection
import org.nrg.xnat.pogo.dqr.PacsPing
import org.nrg.xnat.pogo.dqr.PacsSearchCriteria
import org.nrg.xnat.pogo.dqr.QueuedPacsRequest
import org.nrg.xnat.rest.SerializationUtils
import org.nrg.xnat.versions.plugins.GeneralPluginRequirement

import java.time.DayOfWeek

import static io.restassured.http.ContentType.JSON

@RequirePlugin(PluginRegistry.DQR_ID)
class DqrSubinterface extends XnatPluginSubinterface {

    public static final String DQR_HISTORY_FRAGMENT = '/dqr/import/history'
    public static final String DQR_SETTINGS_FRAGMENT = '/dqr/settings'
    public static final String DQR_PROJECT_SETTINGS_FRAGMENT = "${DQR_SETTINGS_FRAGMENT}/project"

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/dqr/import',
                '/xapi/dqr/import/history',
                '/xapi/dqr/import/history/{id}',
                '/xapi/dqr/import/history/count',
                '/xapi/dqr/import/queue',
                '/xapi/dqr/query/batch',
                '/xapi/dqr/query/patients',
                '/xapi/dqr/query/series',
                '/xapi/dqr/query/studies',
                '/xapi/dqr/settings',
                '/xapi/dqr/settings/project',
                '/xapi/dqr/settings/project/{projectId}',
                '/xapi/dqr/settings/project/{projectId}/enabled',
                '/xapi/pacs',
                '/xapi/pacs/{pacsId}',
                '/xapi/pacs/{pacsId}/status',
                '/xapi/pacs/{pacsId}/availability',
                '/xapi/pacs/{pacsId}/availability/{availabilityId}',
        ]
    }

    @Override
    GeneralPluginRequirement pluginRequirement() {
        new GeneralPluginRequirement().minimumPluginVersion('2.0').pluginId(PluginRegistry.DQR_ID)
    }

    @RequireAdmin
    List<PacsConnection> readAllPacsConnections(Boolean queryable = false, Boolean storable = false) {
        queryBase()
                .queryParams([
                        'queryable': queryable,
                        'storable': storable
                ]).get(formatXapiUrl('pacs'))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(PacsConnection[])
    }

    @RequireAdmin
    int registerPacs(PacsConnection pacs) {
        queryBase()
                .contentType(JSON)
                .body(pacs)
                .post(formatXapiUrl('pacs'))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(PacsConnection)
                .id
    }

    @RequireAdmin
    int updatePacsConnection(PacsConnection pacs) {
        queryBase()
                .contentType(JSON)
                .body(pacs)
                .put(formatXapiUrl("/pacs/${pacs.id}"))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(PacsConnection)
                .id
    }

    @RequireAdmin
    int createOrUpdatePacsConnection(PacsConnection pacs) {
        final Integer preexistingId = readAllPacsConnections().find { connection ->
            connection.aeTitle == pacs.aeTitle
        }?.id
        if (preexistingId) {
            updatePacsConnection(pacs.id(preexistingId))
        } else {
            registerPacs(pacs)
        }
    }

    PacsPing pingPacs(int pacsId) {
        queryBase()
                .get(formatXapiUrl("/pacs/${pacsId}/status"))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(PacsPing)
    }

    @RequireAdmin
    Map<DayOfWeek, List<PacsAvailability>> readPacsAvailability(int pacsId) {
        queryBase()
                .get(formatXapiUrl("/pacs/${pacsId}/availability"))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(new TypeRef<Map<DayOfWeek, List<PacsAvailability>>>() {})
    }

    @RequireAdmin
    List<PacsAvailability> readPacsAvailabilityFlattened(int pacsId) {
        readPacsAvailability(pacsId)*.value.flatten() as List<PacsAvailability>
    }

    @RequireAdmin
    XnatInterface configurePacsAvailability(PacsAvailability availability) {
        queryBase()
                .contentType(JSON)
                .body(availability)
                .post(formatXapiUrl('pacs', String.valueOf(availability.pacsId), 'availability'))
                .then()
                .assertThat()
                .statusCode(200)
        xnatInterface
    }

    @RequireAdmin
    XnatInterface deletePacsAvailability(int pacsId, int scheduleId) {
        queryBase()
                .delete(formatXapiUrl("/pacs/${pacsId}/availability/${scheduleId}"))
                .then()
                .assertThat()
                .statusCode(200)
        xnatInterface
    }

    @RequireAdmin
    XnatInterface deletePacsConnection(PacsConnection pacs) {
        deletePacsConnection(pacs.id)
    }

    @RequireAdmin
    XnatInterface deletePacsConnection(int pacsId) {
        queryBase()
                .delete(formatXapiUrl("pacs/${pacsId}"))
                .then()
                .assertThat()
                .statusCode(200)
        xnatInterface
    }
    List<ExecutedPacsRequest> readDqrQueue(DqrQueueHistoryParams params = null) {
        queryBaseWithCommonStatusCodeListeners()
                .queryParams(SerializationUtils.serializeToMap(params ?: [:]))
                .get(formatXapiUrl("/dqr/import/queue"))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(ExecutedPacsRequest[])
    }

    List<ExecutedPacsRequest> readDqrHistory(DqrQueueHistoryParams params = null) {
        queryBaseWithCommonStatusCodeListeners()
                .queryParams(SerializationUtils.serializeToMap(params ?: [:]))
                .get(formatXapiUrl(DQR_HISTORY_FRAGMENT))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(ExecutedPacsRequest[])
    }

    ExecutedPacsRequest readDqrHistoryItem(int id) {
        queryBaseWithCommonStatusCodeListeners()
                .get(formatXapiUrl(DQR_HISTORY_FRAGMENT, String.valueOf(id)))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(ExecutedPacsRequest)
    }

    int getDqrHistoryCount(Boolean all = null) {
        queryBaseWithCommonStatusCodeListeners()
                .queryParams(all != null ? ['all': all] : [:])
                .get(DQR_HISTORY_FRAGMENT, 'count')
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(Integer)
    }

    @RequireAdmin
    DqrSettings readDqrSettings() {
        queryBaseWithCommonStatusCodeListeners()
                .get(formatXapiUrl(DQR_SETTINGS_FRAGMENT))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(appropriateDqrSettingsClass())
    }

    @RequireAdmin
    XnatInterface setDqrSettings(DqrSettings dqrSettings) {
        queryBaseWithCommonStatusCodeListeners()
                .contentType(JSON)
                .body(prepareSettingsForPost(dqrSettings))
                .post(formatXapiUrl(DQR_SETTINGS_FRAGMENT))
                .then()
                .assertThat()
                .statusCode(200)
        xnatInterface
    }

    @RequireAdmin
    List<DqrProjectSettings> readDqrProjectSettings() {
        queryBaseWithCommonStatusCodeListeners()
                .get(formatXapiUrl(DQR_PROJECT_SETTINGS_FRAGMENT))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(DqrProjectSettings[])
    }

    @RequireAdmin
    XnatInterface enableDqrForProject(Project project) {
        setDqrStatusOnProject(project, true)
    }

    @RequireAdmin
    XnatInterface disableDqrForProject(Project project) {
        setDqrStatusOnProject(project, false)
    }

    @RequireAdmin
    XnatInterface setDqrStatusOnProject(Project project, boolean status) {
        setProjectDqrConfig(project, ['enabled': status])
        xnatInterface
    }

    DqrPatientSearchResponse patientCFind(PacsSearchCriteria searchCriteria) {
        queryBaseWithCommonStatusCodeListeners()
                .contentType(JSON)
                .body(searchCriteria)
                .post(formatXapiUrl('/dqr/query/patients'))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(DqrPatientSearchResponse)
    }

    List<DqrStudyRepresentation> studyCFind(PacsSearchCriteria searchCriteria) {
        queryBaseWithCommonStatusCodeListeners()
                .contentType(JSON)
                .body(searchCriteria)
                .post(formatXapiUrl('/dqr/query/studies'))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(DqrStudyRepresentation[])
    }

    List<DqrCFindRow> studyCFindByCsv(long pacsId, File csv, Boolean allowRowThatGetsAllStudiesOnPacs = null) {
        final Map<String, Object> formParams = ['pacsId': pacsId]
        formParams.put('allowRowThatGetsAllStudiesOnPacs', allowRowThatGetsAllStudiesOnPacs)
        queryBaseWithCommonStatusCodeListeners()
                .formParams(formParams)
                .multiPart("csv_to_store", csv)
                .post(formatXapiUrl('/dqr/query/batch'))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(DqrCFindRow[])
    }

    DqrSeriesSearchResponse aggregatedSeriesCFinds(long pacsId, List<String> studyInstanceUids) {
        queryBaseWithCommonStatusCodeListeners()
                .contentType(JSON)
                .body(['pacsId': pacsId, 'studyInstanceUids': studyInstanceUids])
                .post(formatXapiUrl('/dqr/query/series'))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(DqrSeriesSearchResponse)
    }

    DqrSeriesSearchResponse seriesCFind(PacsConnection pacsConnection, List<String> studyInstanceUids) {
        aggregatedSeriesCFinds(pacsConnection.id, studyInstanceUids)
    }

    List<QueuedPacsRequest> issueCMove(DqrCMoveSpec cMoveSpec) {
        queryBaseWithCommonStatusCodeListeners()
                .contentType(JSON)
                .body(cMoveSpec)
                .post(formatXapiUrl('/dqr/import'))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(QueuedPacsRequest[])
    }

    DqrProjectSettings readDqrForProject(Project project) {
        queryBaseWithCommonStatusCodeListeners()
                .get(formatXapiUrl(DQR_PROJECT_SETTINGS_FRAGMENT, project.id))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(DqrProjectSettings)
    }

    boolean readDqrEnabledStatusOnProject(Project project) {
        queryBaseWithCommonStatusCodeListeners()
                .get(formatXapiUrl(DQR_PROJECT_SETTINGS_FRAGMENT, project.id, 'enabled'))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
    }

    Class<? extends DqrSettings> appropriateDqrSettingsClass() {
        DqrSettings
    }

    DqrSettings prepareSettingsForPost(DqrSettings dqrSettings) {
        dqrSettings
    }

    private void setProjectDqrConfig(Project project, Map<String, ?> config) {
        queryBaseWithCommonStatusCodeListeners()
                .contentType(JSON)
                .body(config)
                .put(formatXapiUrl(DQR_PROJECT_SETTINGS_FRAGMENT, project.id))
                .then()
                .assertThat()
                .statusCode(200)
    }

}
