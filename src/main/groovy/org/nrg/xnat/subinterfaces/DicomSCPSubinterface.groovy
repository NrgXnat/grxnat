package org.nrg.xnat.subinterfaces

import org.nrg.xnat.meta.RequireAdmin
import org.nrg.xnat.pogo.dicom.DicomScpReceiver

import static com.jayway.restassured.http.ContentType.JSON

class DicomSCPSubinterface extends XnatFunctionalitySubinterface {
    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/dicomscp/{id}'
        ]
    }
    @RequireAdmin
    DicomScpReceiver getDefaultDicomSCPInstance() {
        queryBase().get(formatXapiUrl("dicomscp", "1")).
                then().assertThat().statusCode(200).extract().as(DicomScpReceiver.class)
    }

    @RequireAdmin
    DicomScpReceiver updateDefaultDicomSCPInstance(DicomScpReceiver receiver) {
        queryBase().contentType(JSON).body(receiver).
                put(formatXapiUrl("dicomscp", "1")).
                then().log().ifError().assertThat().statusCode(200).extract().as(DicomScpReceiver.class)
    }
}
