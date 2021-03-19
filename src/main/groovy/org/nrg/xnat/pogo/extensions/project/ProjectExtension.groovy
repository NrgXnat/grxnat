package org.nrg.xnat.pogo.extensions.project

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.Project

abstract class ProjectExtension extends Extension<Project> {

    ProjectExtension(Project project) {
        super(project)
    }

    abstract void create(XnatInterface xnatInterface)

}
