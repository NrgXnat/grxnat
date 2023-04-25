package org.nrg.xnat.subinterfaces

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.meta.RequireAdmin
import org.nrg.xnat.pogo.dicom.DicomScpReceiver
import org.nrg.xnat.rest.SerializationUtils

import static io.restassured.http.ContentType.JSON

class DicomSCPSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/dicomscp',
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
    List<DicomScpReceiver> readAllDicomScpReceivers() {
        SerializationUtils.deserializeList(
                queryBase().get(formatXapiUrl('dicomscp')).then().assertThat().statusCode(200).extract().jsonPath().getList(''),
                DicomScpReceiver
        )
    }

    @RequireAdmin
    DicomScpReceiver updateDicomScpReceiver(DicomScpReceiver receiver) {
        queryBase().contentType(JSON).body(receiver).
                put(formatXapiUrl('dicomscp', String.valueOf(receiver.id))).
                then().assertThat().statusCode(200).extract().as(DicomScpReceiver)
    }

    DicomScpReceiver updateDefaultDicomSCPInstance(DicomScpReceiver receiver) {
        updateDicomScpReceiver(receiver)
    }

    @RequireAdmin
    DicomScpReceiver createDicomScpReceiver(DicomScpReceiver receiver) {
        final DicomScpReceiver receiverInResp = queryBase().contentType(JSON).body(receiver).post(formatXapiUrl('dicomscp')).
                then().assertThat().statusCode(200).extract().as(DicomScpReceiver)
        receiver.setId(receiverInResp.id)
        receiverInResp
    }

    @RequireAdmin
    XnatInterface deleteDicomScpReceiver(DicomScpReceiver receiver) {
        queryBase().delete(formatXapiUrl('dicomscp', String.valueOf(receiver.id))).then().assertThat().statusCode(200)
        xnatInterface
    }

    @RequireAdmin
    DicomScpReceiver createOrUpdateDicomScpReceiver(DicomScpReceiver receiver) {
        final Integer preexistingId = readAllDicomScpReceivers().find { candidate ->
            candidate.aeTitle == receiver.aeTitle
        }?.id
        if (preexistingId) {
            updateDicomScpReceiver(receiver.id(preexistingId))
        } else {
            createDicomScpReceiver(receiver)
        }
    }

}
