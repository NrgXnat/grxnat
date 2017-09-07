package org.nrg.xnat.pogo

class Investigator extends Extensible<Investigator> {
    String title
    String firstname
    String lastname
    String institution
    String department
    String email
    String phone
    private int xnatInvestigatordataId

    Investigator() {}

    String getFullName() {
        (firstname != null && lastname != null) ? "${lastname}, ${firstname}" : null
    }

    int getXnatInvestigatordataId() {
        return xnatInvestigatordataId
    }

    void setXnatInvestigatordataId(int xnatInvestigatordataId) {
        this.xnatInvestigatordataId = xnatInvestigatordataId
    }

    void setXnatInvestigatordataId(String xnatInvestigatordataId) {
        this.xnatInvestigatordataId = Integer.parseInt(xnatInvestigatordataId)
    }

    Extension<Investigator> getExtension() {
        return super.getExtension()
    }

    void setExtension(Extension<Investigator> extension) {
        super.setExtension(extension)
    }

    Investigator title(String title) {
        setTitle(title)
        return this
    }

    Investigator firstname(String firstname) {
        setFirstname(firstname)
        return this
    }

    Investigator lastname(String lastname) {
        setLastname(lastname)
        return this
    }

    Investigator institution(String institution) {
        setInstitution(institution)
        return this
    }

    Investigator department(String department) {
        setDepartment(department)
        return this
    }

    Investigator phone(String phone) {
        setPhone(phone)
        return this
    }

    Investigator email(String email) {
        setEmail(email)
        return this
    }

    Investigator id(int id) {
        setXnatInvestigatordataId(id)
        return this
    }

    Investigator extension(Extension<Investigator> extension) {
        setExtension(extension)
        return this
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Investigator that = (Investigator) o

        if (firstname != that.firstname) return false
        if (lastname != that.lastname) return false

        return true
    }

    int hashCode() {
        int result
        result = (firstname != null ? firstname.hashCode() : 0)
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0)
        return result
    }

    @Override
    String toString() {
        getFullName()
    }

}
