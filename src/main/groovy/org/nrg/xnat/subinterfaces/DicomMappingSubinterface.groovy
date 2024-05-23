package org.nrg.xnat.subinterfaces

import io.restassured.http.ContentType
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.meta.RequireAdmin
import org.nrg.xnat.pogo.dicom.DicomMapping

class DicomMappingSubinterface extends CoreXnatFunctionalitySubinterface {

    private static final String DICOM_MAPPING = 'dicom_mappings'

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/dicom_mappings',
                '/xapi/dicom_mappings/{id}',
                '/xapi/dicom_mappings/update'
        ]
    }

    @RequireAdmin
    List<DicomMapping> readDicomMappings() {
        queryBaseWithCommonStatusCodeListeners()
            .get(formatXapiUrl(DICOM_MAPPING))
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .as(DicomMapping[])
    }

    @RequireAdmin
    XnatInterface deleteDicomMapping(long id) {
        queryBaseWithCommonStatusCodeListeners()
            .delete(formatXapiUrl(DICOM_MAPPING, String.valueOf(id)))
            .then()
            .assertThat()
            .statusCode(200)
        xnatInterface
    }

    @RequireAdmin
    XnatInterface deleteDicomMapping(DicomMapping dicomMapping) {
        if (dicomMapping.id == null) {
            dicomMapping.id = readDicomMappings().find { mapping ->
                mapping.fieldName == dicomMapping.fieldName && mapping.schemaElement == dicomMapping.schemaElement
            }.id
        }
        deleteDicomMapping(dicomMapping.id)
    }

    @RequireAdmin
    XnatInterface createOrUpdateDicomMapping(DicomMapping dicomMapping) {
        queryBaseWithCommonStatusCodeListeners()
            .contentType(ContentType.JSON)
            .body(dicomMapping)
            .put(formatXapiUrl(DICOM_MAPPING, 'update'))
            .then()
            .assertThat()
            .statusCode(200)
        xnatInterface
    }

}
