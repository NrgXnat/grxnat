package org.nrg.xnat.pogo.users

import org.nrg.xnat.enums.SiteDataRole
import org.nrg.xnat.pogo.Extensible
import org.nrg.xnat.pogo.Extension

class User extends Extensible<User> {

    String username
    String password
    String email
    String firstName
    String lastName
    boolean verified
    boolean enabled
    boolean admin = false
    SiteDataRole dataRole = SiteDataRole.NONE

    public static final User GUEST = new User('guest')

    User(String username) {
        this.username = username
    }

    User() {}

    void setAdmin(boolean adminStatus) {
        admin = adminStatus
        if (admin) {
            setDataRole(SiteDataRole.ALL_DATA_ADMIN)
        }
    }

    Extension<User> getExtension() {
        super.getExtension()
    }

    void setExtension(Extension<User> extension) {
        super.setExtension(extension)
    }

    User username(String username) {
        setUsername(username)
        this
    }

    User password(String password) {
        setPassword(password)
        this
    }

    User email(String email) {
        setEmail(email)
        this
    }

    User firstName(String firstName) {
        setFirstName(firstName)
        this
    }

    User lastName(String lastName) {
        setLastName(lastName)
        this
    }

    User verified(boolean verified) {
        setVerified(verified)
        this
    }

    User enabled(boolean enabled) {
        setEnabled(enabled)
        this
    }

    User admin(boolean admin) {
        setAdmin(admin)
        this
    }

    User dataRole(SiteDataRole role) {
        setDataRole(role)
        this
    }

    User extension(Extension<User> extension) {
        setExtension(extension)
        this
    }

    @Override
    String toString() {
        "${lastName}, ${firstName} (${username})"
    }

}
