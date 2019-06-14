package org.nrg.xnat.pogo.users

import org.nrg.xnat.enums.DataAccessLevel
import org.nrg.xnat.pogo.DataType

abstract class UserGroup {

    abstract String singularName()
    abstract String pluralName()

    final Map<DataType, DataAccessLevel> accessLevelMap = [:]

    Map<DataType, DataAccessLevel> getAccessLevelMap() {
        accessLevelMap
    }

    DataAccessLevel getAccessLevel(DataType dataType) {
        accessLevelMap.get(dataType) ?: DataAccessLevel.NONE
    }

    void setPermission(DataType dataType, DataAccessLevel accessLevel) {
        accessLevelMap.put(dataType, accessLevel)
    }

    UserGroup permission(DataType dataType, DataAccessLevel accessLevel) {
        setPermission(dataType, accessLevel)
        this
    }

    String groupIdSuffix() {
        singularName().toLowerCase()
    }

}
