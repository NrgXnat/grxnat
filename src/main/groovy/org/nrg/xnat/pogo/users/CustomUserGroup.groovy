package org.nrg.xnat.pogo.users

class CustomUserGroup extends UserGroup {

    String name

    CustomUserGroup(String name) {
        setName(name)
    }

    @Override
    String singularName() {
        name
    }

    @Override
    String pluralName() {
        name
    }

}
