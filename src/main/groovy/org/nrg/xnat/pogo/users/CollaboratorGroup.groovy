package org.nrg.xnat.pogo.users

import org.nrg.xnat.enums.DataAccessLevel
import org.nrg.xnat.pogo.DataType

class CollaboratorGroup extends UserGroup {

    @Override
    String singularName() {
        "Collaborator"
    }

    @Override
    String pluralName() {
        "Collaborators"
    }

    @Override
    DataAccessLevel getAccessLevel(DataType dataType) {
        DataAccessLevel.READ_ONLY
    }

}
