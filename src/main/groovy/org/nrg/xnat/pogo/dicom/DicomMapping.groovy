package org.nrg.xnat.pogo.dicom


import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.nrg.testing.DicomUtils
import org.nrg.xnat.enums.VariableType
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Project

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class DicomMapping {

    Long id
    DicomMappingScope scope
    String scopeObjectId
    String fieldName
    VariableType fieldType
    String dicomTag
    String schemaElement

    DicomMapping forSite() {
        scope(DicomMappingScope.SITE)
    }

    DicomMapping forProject(Project project) {
        scopeObjectId = project.id
        scope(DicomMappingScope.PROJECT)
    }

    DicomMapping dicomTag(int tag) {
        dicomTag('0x' + DicomUtils.intToSimpleHeaderString(tag))
    }

    DicomMapping forDataType(DataType dataType) {
        schemaElement(dataType.xsiType)
    }

}
