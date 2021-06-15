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
    DicomScpReceiver readDicomScpReceiver(int id) {
        queryBase().get(formatXapiUrl('dicomscp', String.valueOf(id))).
                then().assertThat().statusCode(200).extract().as(DicomScpReceiver)
    }

    DicomScpReceiver getDefaultDicomSCPInstance() {
        readDicomScpReceiver(1)
    }

    @RequireAdmin
    DicomScpReceiver updateDicomScpReceiver(DicomScpReceiver receiver, int id) {
        queryBase().contentType(JSON).body(receiver).
                put(formatXapiUrl('dicomscp', String.valueOf(id))).
                then().assertThat().statusCode(200).extract().as(DicomScpReceiver)
    }

    DicomScpReceiver updateDefaultDicomSCPInstance(DicomScpReceiver receiver) {
        updateDicomScpReceiver(receiver, 1)
    }

}
