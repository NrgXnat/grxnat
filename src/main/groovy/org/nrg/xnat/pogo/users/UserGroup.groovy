package org.nrg.xnat.pogo.users

import org.nrg.xnat.enums.DataAccessLevel
import org.nrg.xnat.pogo.DataType

abstract class UserGroup {

    abstract String singularName()
    abstract String pluralName()

    protected final Map<DataType, DataAccessLevel> accessLevelMap = [:]

    DataAccessLevel getAccessLevel(DataType dataType) {
        return accessLevelMap.get(dataType)
    }

}
