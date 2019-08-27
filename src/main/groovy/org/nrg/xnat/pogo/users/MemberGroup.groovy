package org.nrg.xnat.pogo.users

import org.nrg.xnat.enums.DataAccessLevel
import org.nrg.xnat.pogo.DataType

class MemberGroup extends UserGroup {

    @Override
    String singularName() {
        'Member'
    }

    @Override
    String pluralName() {
        'Members'
    }

    @Override
    DataAccessLevel getAccessLevel(DataType dataType) {
        DataAccessLevel.CREATE_AND_EDIT
    }

}
