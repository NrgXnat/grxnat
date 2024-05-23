package org.nrg.xnat.pogo.dicom

import com.fasterxml.jackson.annotation.JsonValue
import org.apache.commons.lang3.StringUtils

enum DicomMappingScope {
    SITE, PROJECT

    @Override
    @JsonValue
    String toString() {
        StringUtils.capitalize(name().toLowerCase())
    }
}
