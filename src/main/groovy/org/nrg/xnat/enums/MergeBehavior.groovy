package org.nrg.xnat.enums

enum MergeBehavior {

    NONE ('none'),
    APPEND ('append'),
    DELETE ('delete'),
    TRUE ('true') // strange option, but seems to be supported. present here to test legacy calls

    String behaviorKey

    MergeBehavior(String key) {
        behaviorKey = key
    }

}