package org.nrg.xnat.enums

enum RoutingRulesType {
    PROJECT_RULES ('projectRules'),
    SUBJECT_RULES ('subjectRules'),
    SESSION_RULES ('sessionRules')

    String configPath

    RoutingRulesType(String path) {
        configPath = path
    }

}