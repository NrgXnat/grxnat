package org.nrg.xnat.enums

enum MergeBehavior {

    NONE ('none'),
    APPEND ('append'),
    DELETE ('delete')

    String behaviorKey

    MergeBehavior(String key) {
        behaviorKey = key
    }

}