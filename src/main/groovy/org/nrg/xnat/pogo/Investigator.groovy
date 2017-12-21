package org.nrg.xnat.pogo

class Investigator extends Extensible<Investigator> {
    String title
    String firstname
    String lastname
    String institution
    String department
    String email
    String phone
    List<String> primaryProjects = []
    int xnatInvestigatordataId

    Investigator() {}

    String getFullName() {
        (firstname != null && lastname != null) ? "${lastname}, ${firstname}" : null
    }

    void setId(String xnatInvestigatordataId) {
        setXnatInvestigatordataId(Integer.parseInt(xnatInvestigatordataId))
    }

    Extension<Investigator> getExtension() {
        super.getExtension()
    }

    void setExtension(Extension<Investigator> extension) {
        super.setExtension(extension)
    }

    Investigator title(String title) {
        setTitle(title)
        this
    }

    Investigator firstname(String firstname) {
        setFirstname(firstname)
        this
    }

    Investigator lastname(String lastname) {
        setLastname(lastname)
        this
    }

    Investigator institution(String institution) {
        setInstitution(institution)
        this
    }

    Investigator department(String department) {
        setDepartment(department)
        this
    }

    Investigator phone(String phone) {
        setPhone(phone)
        this
    }

    Investigator email(String email) {
        setEmail(email)
        this
    }

    Investigator id(int id) {
        setXnatInvestigatordataId(id)
        this
    }

    Investigator primaryProjects(List<String> projects) {
        setPrimaryProjects(projects)
        this
    }

    Investigator extension(Extension<Investigator> extension) {
        setExtension(extension)
        this
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
        result
    }

    @Override
    String toString() {
        getFullName()
    }

}
