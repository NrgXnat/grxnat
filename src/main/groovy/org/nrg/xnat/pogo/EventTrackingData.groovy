package org.nrg.xnat.pogo

import javax.annotation.Nullable

class EventTrackingData {

    String key
    @Nullable String payload
    @Nullable Boolean succeeded
    @Nullable String finalMessage

}
