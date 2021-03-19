package org.nrg.xnat.pogo.extensions.subject

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject

abstract class SubjectExtension extends Extension<Subject> {

    SubjectExtension(Subject subject) {
        super(subject)
    }

    abstract void create(XnatInterface xnatInterface, Project project)

}
