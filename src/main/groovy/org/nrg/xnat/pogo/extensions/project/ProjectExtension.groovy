package org.nrg.xnat.pogo.extensions.project

import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.users.User

abstract class ProjectExtension extends Extension<Project> {

    ProjectExtension(Project project) {
        super(project)
    }

    abstract void create()

}
