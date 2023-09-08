package org.nrg.xnat.pogo.dicom

import com.fasterxml.jackson.annotation.JsonValue

enum FilterMode {

    BLACKLIST ('blacklist'),
    WHITELIST ('whitelist'),
    MODALITY_MAP ('modalityMap')

    String representation

    FilterMode(String representation) {
        setRepresentation(representation)
    }

    @JsonValue
    String jsonRepresentation() {
        representation
    }

    static lookup(String representation) {
        values().find { mode ->
            mode.representation == representation
        }
    }

}