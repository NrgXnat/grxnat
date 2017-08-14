package org.nrg.xnat.pogo

class Share extends Extensible<Share> {

    String destinationProject
    String destinationLabel

    Share(String project, String label) {
        destinationProject = project
        destinationLabel = label
    }

    Share(Project project, String label) {
        this(project.getId(), label)
    }

    Share(String project) {
        this(project, null)
    }

    Share(Project project) {
        this(project.getId())
    }

    Share() {}

    Extension<Share> getExtension() {
        return super.getExtension()
    }

    void setExtension(Extension<Share> extension) {
        super.setExtension(extension)
    }

    Share destinationProject(String project) {
        setDestinationProject(project)
        return this
    }

    Share destinationLabel(String label) {
        setDestinationLabel(label)
        return this
    }

    Share extension(Extension<Share> extension) {
        setExtension(extension)
        return this
    }

}
