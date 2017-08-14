package org.nrg.xnat.enums

enum DataAccessLevel {
    NONE,
    READ_ONLY,
    CREATE_AND_EDIT, // implies read access
    DELETE, // implies read access, but not create/edit
    ALL
}
