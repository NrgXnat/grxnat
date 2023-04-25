package org.nrg.xnat.subinterfaces

import io.restassured.common.mapper.TypeRef
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.meta.RequireAdmin
import org.nrg.xnat.meta.RequirePlugin
import org.nrg.xnat.pogo.PluginRegistry
import org.nrg.xnat.pogo.dqr.PacsAvailability
import org.nrg.xnat.pogo.dqr.PacsConnection

import java.time.DayOfWeek

import static io.restassured.http.ContentType.JSON

@RequirePlugin(PluginRegistry.DQR_ID)
class DqrSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/pacs',
                '/xapi/pacs/{pacsId}',
                '/xapi/pacs/{pacsId}/availability',
                '/xapi/pacs/{pacsId}/availability/{availabilityId}'
        ]
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
                .as(List<PacsConnection>)
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

}
