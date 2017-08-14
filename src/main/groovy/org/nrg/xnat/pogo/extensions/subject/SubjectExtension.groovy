package org.nrg.xnat.pogo.extensions.subject

import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.users.User

abstract class SubjectExtension extends Extension<Subject> {

    SubjectExtension(Subject subject) {
        super(subject)
    }

    abstract void create(Project project)

}
