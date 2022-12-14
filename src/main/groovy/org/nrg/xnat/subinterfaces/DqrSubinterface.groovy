package org.nrg.xnat.subinterfaces

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.meta.RequireAdmin
import org.nrg.xnat.meta.RequirePlugin
import org.nrg.xnat.pogo.PluginRegistry
import org.nrg.xnat.pogo.dqr.PacsAvailability
import org.nrg.xnat.pogo.dqr.PacsConnection

import static io.restassured.http.ContentType.JSON

@RequirePlugin(PluginRegistry.DQR_ID)
class DqrSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/pacs',
                '/xapi/pacs/{pacsId}/availability'
        ]
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

}
