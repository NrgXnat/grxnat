package org.nrg.xnat.pogo.resources

import org.nrg.xnat.pogo.Project

class ProjectResource extends Resource {

    @Override
    String resourceUrl() {
        "data/projects/${project}"
    }

    ProjectResource(Project project, String label) {
        setProject(project)
        setFolder(label)
        if (project != null) {
            project.addResource(this)
        }
    }

    ProjectResource() {
        super()
    }

}
