package org.nrg.xnat.pogo.users

import org.nrg.xnat.enums.DataAccessLevel
import org.nrg.xnat.pogo.DataType

class OwnerGroup extends UserGroup {

    @Override
    String singularName() {
        'Owner'
    }

    @Override
    String pluralName() {
        'Owners'
    }

    @Override
    DataAccessLevel getAccessLevel(DataType dataType) {
        DataAccessLevel.ALL
    }

}
