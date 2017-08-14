package org.nrg.xnat.pogo.users

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

    User(String username) {
        this.username = username
    }

    User() {}

    Extension<User> getExtension() {
        return super.getExtension()
    }

    void setExtension(Extension<User> extension) {
        super.setExtension(extension)
    }

    User username(String username) {
        setUsername(username)
        return this
    }

    User password(String password) {
        setPassword(password)
        return this
    }

    User email(String email) {
        setEmail(email)
        return this
    }

    User firstName(String firstName) {
        setFirstName(firstName)
        return this
    }

    User lastName(String lastName) {
        setLastName(lastName)
        return this
    }

    User verified(boolean verified) {
        setVerified(verified)
        return this
    }

    User enabled(boolean enabled) {
        setEnabled(enabled)
        return this
    }

    User admin(boolean admin) {
        setAdmin(admin)
        return this
    }

    User extension(Extension<User> extension) {
        setExtension(extension)
        return this
    }

    @Override
    String toString() {
        "${lastName}, ${firstName} (${username})"
    }

}
