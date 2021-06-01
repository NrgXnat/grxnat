package org.nrg.xnat.enums

import com.fasterxml.jackson.annotation.JsonValue

enum PetMrProcessingSetting {

    DEFAULT_TO_SITE (null, 'Use the system default'),
    AS_PET_MR ('petmr', 'Create as PET/MR session'),
    AS_PET ('pet', 'Create as PET session'),
    SPLIT ('separate', 'Separate into PET and MR sessions')

    String apiValue
    String uiDisplayText

    PetMrProcessingSetting(String apiValue, String text) {
        setApiValue(apiValue)
        setUiDisplayText(text)
    }

    @JsonValue
    String jsonRepresentation() {
        apiValue
    }

}